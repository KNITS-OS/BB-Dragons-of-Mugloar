package com.bigbank.mugloar.service.core;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;
import com.bigbank.mugloar.exception.GameOverException;
import com.bigbank.mugloar.exception.MissionNotFoundException;
import com.bigbank.mugloar.exception.MissionOutOfSyncException;
import com.bigbank.mugloar.mappers.MissionMapper;
import com.bigbank.mugloar.model.Mission;
import com.bigbank.mugloar.model.MissionOutcomeType;
import com.bigbank.mugloar.model.MissionResultType;
import com.bigbank.mugloar.proxy.MissionProxy;
import com.bigbank.mugloar.repository.core.MissionRepository;
import com.bigbank.mugloar.service.game.GameStateService;
import com.bigbank.mugloar.util.EncryptionUtils;
import com.bigbank.mugloar.util.MissionConsts;
import com.bigbank.mugloar.util.comparator.MissionComparator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class MissionService {

    private final MissionProxy missionProxy;
    private final MissionMapper missionMapper;
    private final MissionRepository missionRepository;

    private final ApplicationProperties appConfig;
    private final GameStateService gameStateService;

    public MissionBatchDto loadNewMissionBatch(String gameId) {
        List<MissionDto> availableMissions = loadAvailableMissionInternal(gameId);

        if (log.isDebugEnabled()) {
            Set<String> missionIds = availableMissions.stream().map(mission -> mission.getAdId()).collect(Collectors.toSet());
            log.debug("Found {} missions with ids: {}", availableMissions.size(), missionIds.toString());
        }
        GameDto gameState = gameStateService.getCurrentGameById(gameId);
        Set<MissionDto> availableMissionsAsSet = availableMissions.stream().map(mission -> addGame(mission, gameState)).collect(Collectors.toSet());

        MissionBatchDto missionBatch = new MissionBatchDto();
        missionBatch.setGameId(gameId);
        missionBatch.setAvailableMissions(availableMissionsAsSet);
        initializeMissionQueues(missionBatch,availableMissions);
        initializeMissionLists(missionBatch);

        gameState.increaseMissionBatchCounter();
        return missionBatch;
    }

    public MissionResultDto executeMission(String gameId, MissionDto currentMission) {
        try {
            log.info("Executing Mission {} ...", currentMission.getAdId());
            ThreadUtils.sleepQuietly(Duration.ofMillis(appConfig.getThrottling().getApiThrottlingDelay()));
            MissionResultDto missionOutcome = missionProxy.takeMission(gameId, currentMission.getAdId());
            checkNotNull(currentMission,missionOutcome);
            updateResultWithMissionData(missionOutcome,currentMission);
            updateMissionWithOutcome(missionOutcome,currentMission);

            validateMissionResponse(currentMission, missionOutcome);
            log.info("Mission {} Executed with outcome {} ", currentMission.getAdId(), missionOutcome.toString());
            return missionOutcome;

        } catch (FeignException e) {
            return handleRetry(e, gameId, currentMission);
        }
    }
    private  List<MissionDto> loadAvailableMissionInternal(String gameId){
        try{
            return missionProxy.getGameAvailableMissions(gameId);
        }  catch (FeignException e) {
            return handleRetryLoadMissions(e, gameId);
        }
    }

    private void updateResultWithMissionData(MissionResultDto missionOutcome,MissionDto currentMission){
        missionOutcome.setMission(currentMission);
        missionOutcome.setGame(currentMission.getGame());
        MissionResultType outcome= (missionOutcome.isSuccess())? MissionResultType.SUCCESS : MissionResultType.FAILURE;
        missionOutcome.setOutcome(outcome);
    }

    private void updateMissionWithOutcome(MissionResultDto missionOutcome,MissionDto currentMission){
        MissionOutcomeType outcome= (missionOutcome.isSuccess())? MissionOutcomeType.SUCCESS : MissionOutcomeType.FAILURE;
        currentMission.setOutcome(outcome);
    }

    private void checkNotNull(MissionDto mission, MissionResultDto missionResult){
        if (missionResult == null) {
            mission.setOutcome(MissionOutcomeType.NOT_AVAILABLE);
            throw new MissionNotFoundException(mission.getGame().getGameId(), mission,
                    format("Mugloar Api return null response for Mission  %s in Game  %s", mission.getAdId(), mission.getGame().getGameId()));
        }
    }
    private void validateMissionResponse(MissionDto mission, MissionResultDto missionResult) {

        GameDto currentGame = gameStateService.getCurrentGameById(mission.getGame().getGameId());
        if (currentGame.getTurn() >= missionResult.getTurn()) {
            missionResult.setOutcome(MissionResultType.OUT_OF_SYNCH);
            mission.setOutcome(MissionOutcomeType.OUT_OF_SYNCH);
            throw new MissionOutOfSyncException(mission.getGame().getGameId(), mission, missionResult,
                    format("Mission Response out of synch for mission %s in Game %s, current Game turn:  %s Response turn:  %s",
                            mission.getAdId(), mission.getGame().getGameId(), currentGame.getTurn(), missionResult.getTurn())
            );
        }
    }

    private List<MissionDto> handleRetryLoadMissions(FeignException e, String gameId) {
        log.info("Call to get Available Missions failed with message: {} with status {} ", e.getMessage(),e.status());
        if (e.status()==410){
            throw new GameOverException("Server abruptly closed the game");
        }
        return loadAvailableMissionInternal(gameId); //only one retry allowed
    }

    private MissionResultDto handleRetry(FeignException e, String gameId, MissionDto lastFailedMission) {
        log.info("Call to service failed: {} ", e.getMessage());
        if (lastFailedMission.getRetryCount() <= 0) {
            GameDto currentGame = gameStateService.getCurrentGameById(gameId);
            currentGame.increaseMissionsNotFoundCounter();
            lastFailedMission.setOutcome(MissionOutcomeType.NOT_AVAILABLE);
            throw new MissionNotFoundException(gameId, lastFailedMission,
                    format("Retry limit reached. No more tries for Mission %s", lastFailedMission.getAdId()));
        }

        return retryMissionExecutionWithDelay(gameId, lastFailedMission);
    }

    private MissionDto addGame(MissionDto original, GameDto currentGame) {
        original.setGame(currentGame);
        return original;
    }

    private void initializeMissionQueues(MissionBatchDto missionBatch, List<MissionDto> availableMissions) {
        Map<String, List<MissionDto>> missionsGroupedByProbability = aggregateFilteredMissionsByProbability(availableMissions);
        missionBatch.setSafeMissions(extractMissionByRiskRange(missionsGroupedByProbability, MissionConsts.SAFE_MISSIONS));
        missionBatch.setEasyMissions(extractMissionByRiskRange(missionsGroupedByProbability, MissionConsts.EASY_MISSIONS));
        missionBatch.setRiskyMissions(extractMissionByRiskRange(missionsGroupedByProbability, MissionConsts.RISKY_MISSIONS));
    }

    private void initializeMissionLists(MissionBatchDto missionBatch) {
        missionBatch.setExecutedMissions(new ArrayList<>());
        missionBatch.setExecutedMissionResults(new ArrayList<>());
        missionBatch.setFailedMissions(new ArrayList<>());
        missionBatch.setFailedMissionsResults(new ArrayList<>());
        missionBatch.setSkippedMissions(new ArrayList<>());
        missionBatch.setSkippedMissionsResults(new ArrayList<>());
    }

    private MissionResultDto retryMissionExecutionWithDelay(String gameId, MissionDto nextAvailableMission) {
        long retryDelay;
        switch (nextAvailableMission.getRetryCount()) {
            case 1:
                retryDelay = 200L;
                break;
            case 2:
                retryDelay = 100L;
                break;
            case 3:
                retryDelay = 50L;
                break;
            default:
                throw new IllegalStateException("Retry count cant be lower or equal to zero");
        }

        log.info("Retry call to mission {} . Retry count: {} ", nextAvailableMission.getAdId(), nextAvailableMission.getRetryCount());
        ThreadUtils.sleepQuietly(Duration.ofMillis(retryDelay));
        nextAvailableMission.decreaseRetryCount();
        return executeMission(gameId, nextAvailableMission);
    }


    private Map<String, List<MissionDto>> aggregateFilteredMissionsByProbability(List<MissionDto> availableMissions) {

        return availableMissions.stream()
                .filter((mission) -> (
                        !MissionConsts.DANGEROUS_MISSIONS.contains(mission.getProbability())) && missionCanBeDecrypted(mission)
                )
                .map(mission -> decodeMission(mission))
                .map(mission -> addWeightedReward(mission))
                .collect(Collectors.groupingBy(MissionDto::getProbability));
    }

    private Queue<MissionDto> extractMissionByRiskRange(Map<String, List<MissionDto>> groupedMissions, Set<String> missionRiskRange) {
        Queue<MissionDto> missionsByRiskRange = new PriorityQueue<>(new MissionComparator());
        for (String probability : missionRiskRange) {
            missionsByRiskRange.addAll(addMissionsByProbability(groupedMissions, probability));
        }
        return missionsByRiskRange;
    }

    private List<MissionDto> addMissionsByProbability(Map<String, List<MissionDto>> groupedMissions, String probability) {
        return groupedMissions.get(probability) == null ? new ArrayList<>() : groupedMissions.get(probability);
    }

    private boolean missionCanBeDecrypted(MissionDto mission) {

        if (mission.getEncrypted() == null) {
            return true;
        }
        if (MissionConsts.ENCRYPTED_PROBABILITY_MAP.get(mission.getProbability()) == null) {
            log.warn("Encrypted mission with probability {} not found in Decrypted Map. Mission will be skipped", mission.getProbability());
            return false;
        }
        return true;
    }

    private MissionDto decodeMission(MissionDto mission) {

        if (mission.getEncrypted() == null) {
            return mission;
        } else {
            String probability = MissionConsts.ENCRYPTED_PROBABILITY_MAP.get(mission.getProbability());
            mission.setProbability(probability);
            mission.setAdId(EncryptionUtils.decryptBase64(mission.getAdId()));
            log.info("Decrypted Mission probabiliy: {} aiId: {} ", probability, mission.getAdId());
            return mission;
        }
    }


    private MissionDto addWeightedReward(MissionDto mission) {
        int weightedReward = (int) (mission.getReward() * MissionConsts.PROBABILITY_PRIORITY_MAP.get(mission.getProbability()));
        mission.setRiskWeightedReward(weightedReward);
        return mission;
    }

    public List<MissionDto> saveAll(List<MissionDto> currentMissionBatch) {
        List<Mission> missionEntities = missionMapper.toEntities(currentMissionBatch);
        return missionMapper.toDtos(missionRepository.saveAll(missionEntities));
    }
}
