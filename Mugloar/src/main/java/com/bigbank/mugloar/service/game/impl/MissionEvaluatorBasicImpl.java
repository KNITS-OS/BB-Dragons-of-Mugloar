package com.bigbank.mugloar.service.game.impl;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;
import com.bigbank.mugloar.exception.MissionBatchException;
import com.bigbank.mugloar.exception.MissionDuplicatedException;
import com.bigbank.mugloar.exception.MissionExpiredException;
import com.bigbank.mugloar.processor.MissionEvaluator;
import com.bigbank.mugloar.service.game.GameStateService;
import com.bigbank.mugloar.util.MissionConsts;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.TreeMap;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class MissionEvaluatorBasicImpl implements MissionEvaluator {

    private final ApplicationProperties appConfig;
    private final GameStateService gameStateService;


    public MissionDto selectNextMissionFromBatch(MissionBatchDto missionBatch) {

        GameDto gameState = gameStateService.getCurrentGameById(missionBatch.getGameId());

        if (gameState.getLives() < appConfig.getStrategy().getLivesAcceptSafeLimit()) {
            MissionDto safeMission = pollSafeMission(missionBatch);
            if (safeMission != null) {
                checkMissionAvailable(gameState,missionBatch,safeMission);
                return safeMission;
            }
        }

        if (gameState.getLives() >= appConfig.getStrategy().getLivesAcceptEasyLimit() &&
                gameState.getLives() < appConfig.getStrategy().getLivesAcceptRiskyLimit()){
            MissionDto easyMission = pollHighestRewardMissionFromEasyQueues(missionBatch);
            if (easyMission != null) {
                checkMissionAvailable(gameState,missionBatch,easyMission);
                return easyMission;
            }
        }

        MissionDto highestRewardMission = pollHighestRewardMissionFromAllQueues(missionBatch);
        if (highestRewardMission == null) {
            throw new MissionBatchException(missionBatch.getGameId(),"No suitable mission in this batch. Needs a new load");
        }
        checkMissionAvailable(gameState,missionBatch,highestRewardMission);
        return highestRewardMission;
    }

    private void checkMissionAvailable(GameDto gameState, MissionBatchDto missionBatch, MissionDto missionSelected){
        if (!missionBatch.isMissionAvailable(missionSelected)) {
            throw new MissionExpiredException(missionSelected.getGame().getGameId(),missionSelected,
                    format("Mission %s for Game %s was expired and will be skipped",
                            missionSelected.getAdId(),missionSelected.getGame().getGameId()));
        }

        if(gameState.getExecutedMissionsIds().contains(missionSelected.getAdId())){
            throw new MissionDuplicatedException(missionSelected.getGame().getGameId(),missionSelected,
                    format("Mission %s for Game %s was already processed and will be skipped",
                            missionSelected.getAdId(),missionSelected.getGame().getGameId()));
        }
    }


    private MissionDto pollSafeMission(MissionBatchDto missionBatch) {
        return missionBatch.getSafeMissions().poll();
    }

    private MissionDto pollHighestRewardMissionFromEasyQueues(MissionBatchDto missionBatch) {
        return getOrderedQueueMapCompareEasy(missionBatch).firstEntry() == null ?
                null : getOrderedQueueMapCompareEasy(missionBatch).firstEntry().getValue().poll();
    }

    private MissionDto pollHighestRewardMissionFromAllQueues(MissionBatchDto missionBatch) {
        return getOrderedQueueMapCompareAll(missionBatch).firstEntry() == null ?
                null : getOrderedQueueMapCompareAll(missionBatch).firstEntry().getValue().poll();
    }

    private TreeMap<MissionDto, Queue<MissionDto>> getOrderedQueueMapCompareAll(MissionBatchDto missionBatch) {
        TreeMap<MissionDto, Queue<MissionDto>> orderedQueues = getOrderedQueueMapCompareEasy(missionBatch);
        addNextMissionQueuePairIntoMap(missionBatch.getRiskyMissions(), orderedQueues);
        return orderedQueues;
    }


    private TreeMap<MissionDto, Queue<MissionDto>> getOrderedQueueMapCompareEasy(MissionBatchDto missionBatch) {
        TreeMap<MissionDto, Queue<MissionDto>> orderedQueues = new TreeMap<>(MissionConsts.WEIGHTED_PRIORITY_COMPARATOR);
        addNextMissionQueuePairIntoMap(missionBatch.getSafeMissions(), orderedQueues);
        addNextMissionQueuePairIntoMap(missionBatch.getEasyMissions(), orderedQueues);
        return orderedQueues;
    }

    private void addNextMissionQueuePairIntoMap(Queue<MissionDto> missionQueue, TreeMap<MissionDto, Queue<MissionDto>> orderedQueues) {
        MissionDto nextSafeMission = missionQueue.peek();
        if (nextSafeMission != null) {
            orderedQueues.put(nextSafeMission, missionQueue);
        }
    }
}
