package com.bigbank.mugloar.service.game.impl;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;
import com.bigbank.mugloar.exception.*;
import com.bigbank.mugloar.model.GameStateType;
import com.bigbank.mugloar.model.MissionOutcomeType;
import com.bigbank.mugloar.processor.ItemProcessor;
import com.bigbank.mugloar.processor.MissionEvaluator;
import com.bigbank.mugloar.processor.MissionProcessor;
import com.bigbank.mugloar.service.core.MissionResultService;
import com.bigbank.mugloar.service.core.MissionService;
import com.bigbank.mugloar.service.game.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class MissionProcessorBasicImpl implements MissionProcessor {

    private final ApplicationProperties appConfig;
    private final MissionEvaluator missionEvaluator;
    private final EventService eventService;
    private final MissionService missionService;
    private final GameStateServiceCaffeineImpl gameStateService;
    private final ItemProcessor itemProcessor;
    private final MissionResultService missionResultService;

    public MissionBatchDto processCurrentMissionBatch(MissionBatchDto missionBatch) {

        String gameId=missionBatch.getGameId();
        GameDto currentGame =gameStateService.getCurrentGameById(gameId);

        while (missionBatch.getAvailableMissions().size() > 0) {

            try{
                MissionDto nextSuitableMission = missionEvaluator.selectNextMissionFromBatch(missionBatch);
                MissionResultDto missionResult= missionService.executeMission(gameId,nextSuitableMission);
                updateMissionBatch(missionBatch,nextSuitableMission,missionResult);
                updateGameStateCounters(currentGame,missionResult);
                eventService.saveExecutedMissionEvent(currentGame, nextSuitableMission, missionResult);
                updateGameStateWithProcessedMission(currentGame,missionResult);
                itemProcessor.upgradeGameWithPowerItems(currentGame);

            }catch (MissionSkipException e){
                handleMissionSkipException(currentGame,missionBatch,e);
            }catch (MissionBatchException e){
                saveIncompleteBatchExecution(e, gameId, missionBatch);
                throw e;
            }
        }
        saveMissionBatchExecution(missionBatch);
        gameStateService.updateCache(currentGame);
        return missionBatch;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMissionBatchExecution(MissionBatchDto processedMissionBatch) {
        if (appConfig.isSaveExecution()) {
            List<MissionDto> missionBatch = missionService.saveAll(joinAllMissions(processedMissionBatch));
            List<MissionResultDto> missionResultBatch = missionResultService.saveAll(joinAllMissionResults(processedMissionBatch));
            log.info("Saved {} missions and {} mission results for game {}", missionBatch.size(), missionResultBatch.size(), processedMissionBatch.getGameId());
        }
    }

    private void saveIncompleteBatchExecution(MissionBatchException e, String gameId, MissionBatchDto missionBatch){
        log.info("Mission Batch for Game {} canceled processing becuase of {}. Will be saved, and a new Batch will be loaded ",
                gameId,e.getMessage());
        saveMissionBatchExecution(missionBatch);
    }



    private void handleMissionSkipException(GameDto currentGame,MissionBatchDto missionBatch,MissionSkipException e){
        log.warn("Mission {} skipped because of {}", e.getMission().getAdId(), e.getMessage());
        missionBatch.getSkippedMissions().add(e.getMission());

        if(e instanceof MissionOutOfSyncException ==true){
            currentGame.increaseMissionsOutOfSyncCounter();
            MissionResultDto result =((MissionOutOfSyncException)e).getMissionResult();
            missionBatch.getSkippedMissionsResults().add(result);
            currentGame.increasePotentialScore(e.getMission().getReward());
            eventService.saveMissionOutOfSynchEvent(currentGame,e.getMission(),result);
            return;
        }

        if(e instanceof MissionExpiredException ==true){
            MissionDto missionExpired =e.getMission();
            missionExpired.setOutcome(MissionOutcomeType.EXPIRED);
            currentGame.increaseMissionExpiredCounter();
            eventService.saveExpiredMissionEvent(currentGame,missionExpired);
            return;
        }

        if(e instanceof MissionDuplicatedException ==true){
            currentGame.increaseMissionExpiredCounter();
            eventService.saveDuplicatedMissionEvent(currentGame,e.getMission());
            return;
        }

        if(e instanceof MissionNotFoundException ==true) {
            missionBatch.increaseMissionNotFoundCounter();
            eventService.saveMissionNotFoundOnServerEvent(currentGame, e.getMission());
            if (missionBatch.getMissionNotFoundCounter() > appConfig.getStrategy().getMissionNotFoundThreshold()) {
                currentGame.increaseNotFoundBatchReloadCounter();
                ThreadUtils.sleepQuietly(Duration.ofMillis(appConfig.getThrottling().getApiThrottlingOnExceptionDelay()));
                throw new MissionBatchException(missionBatch.getGameId(),
                        format("Too many missions were not found in Mission Batch for Game {}. New request is needed",
                                missionBatch.getGameId()));
            }
        }
    }

    private void updateGameStateWithProcessedMission(GameDto currentGame, MissionResultDto missionResult) {
        currentGame.setLives(missionResult.getLives());
        currentGame.setGold(missionResult.getGold());
        currentGame.setScore(missionResult.getScore());
        gameStateService.updateCache(currentGame);

        if(!currentGame.isRunning()){
            String gameOutcome =currentGame.isVictory()? GameStateType.VICTORY.name() : GameStateType.DEFEAT.name();
            throw new GameOverException(format("Successfully ended game with outcome: %s ",gameOutcome));
        }
    }

    private void updateGameStateCounters(GameDto currentGame, MissionResultDto missionResult) {
        currentGame.setTurn(missionResult.getTurn());
        currentGame.increaseOperationCounter();

        if(missionResult.isSuccess()){
            currentGame.increaseMissionExecutedCounter();
        }else {
            currentGame.increaseMissionFailedCounter();
        }
    }


    private void updateMissionBatch(MissionBatchDto missionBatch, MissionDto lastMission,MissionResultDto missionResult) {

        missionBatch.increaseMissionExecutionCounter();

        if(missionResult.isSuccess()){
            missionBatch.getExecutedMissions().add(lastMission);
            missionBatch.getExecutedMissionResults().add(missionResult);
        }else{
            missionBatch.getFailedMissions().add(lastMission);
            missionBatch.getFailedMissionsResults().add(missionResult);
        }
    }

    private List<MissionDto> joinAllMissions(MissionBatchDto processedMissionBatch){
        List<MissionDto> allMissions= new ArrayList<>();
        allMissions.addAll(processedMissionBatch.getExecutedMissions());
        allMissions.addAll(processedMissionBatch.getFailedMissions());
        allMissions.addAll(processedMissionBatch.getSkippedMissions());
        return allMissions;
    }

    private List<MissionResultDto> joinAllMissionResults(MissionBatchDto processedMissionBatch){
        List<MissionResultDto> allMissionResults= new ArrayList<>();
        allMissionResults.addAll(processedMissionBatch.getExecutedMissionResults());
        allMissionResults.addAll(processedMissionBatch.getFailedMissionsResults());
        allMissionResults.addAll(processedMissionBatch.getSkippedMissionsResults());
        return allMissionResults;
    }

}
