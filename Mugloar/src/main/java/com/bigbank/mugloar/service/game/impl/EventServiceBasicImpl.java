package com.bigbank.mugloar.service.game.impl;

import com.bigbank.mugloar.dto.domain.core.*;
import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.mappers.EventMapper;
import com.bigbank.mugloar.model.EventType;
import com.bigbank.mugloar.repository.game.EventRepository;
import com.bigbank.mugloar.service.game.EventService;
import com.bigbank.mugloar.util.EventConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventServiceBasicImpl implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    public List<EventDto> findEventsByGameId(String gameId) {
        return eventMapper.toDtos(eventRepository.findAllByGameExternalId(gameId));
    }

    public EventDto saveStartGameEvent(GameDto gameState) {

        EventDto startEvent = startEndEventsCommon(gameState);
        startEvent.setOperationCounter(EventConsts.FIRST_OPERATION);
        startEvent.setType(EventType.GAME_START);
        startEvent.setOutcome(EventConsts.GAME_STARTED);
        return saveEvent(startEvent);
    }

    public EventDto saveExecutedMissionEvent(GameDto gameState, MissionDto mission, MissionResultDto missionResult) {

        String outcome = missionResult.isSuccess() ? EventConsts.MISSION_SUCCESS : EventConsts.MISSION_FAILED;

        EventDto executedMission = EventDto.builder().
                gameExternalId(gameState.getGameId()).
                eventExternalId(mission.getAdId()).
                missionBatchCounter(gameState.getMissionBatchCounter()).
                gameLives(gameState.getLives()).
                responseLives(missionResult.getLives()).
                gameScore(gameState.getScore()).
                responseScore(missionResult.getScore()).
                gameLevel(gameState.getLevel()).
                responseLevel(-1).
                gameGold(gameState.getGold()).
                responseGold(missionResult.getGold()).
                gameTurn(gameState.getTurn()).
                responseTurn(missionResult.getTurn()).
                operationAmount(mission.getReward()).
                operationCounter(gameState.getOperationCounter()).
                highScore(missionResult.getHighScore()).
                type(EventType.EXECUTE_MISSION).
                outcome(outcome).
                build();
        return saveEvent(executedMission);
    }

    public EventDto savePurchasedItemEvent(GameDto gameState, ItemDto item, PurchasedItemDto itemPurchased) {

        if (itemPurchased.getScore() != gameState.getScore()) {
            log.warn("Game and Item Purchase result have inconsistent values: Game Score {} Mission Result {} ", gameState.getScore(), itemPurchased.getScore());
        }
        int cost = (int) (item.getCost() * -1);

        String outcome = Boolean.valueOf(itemPurchased.getShoppingSuccess()) ? EventConsts.ITEM_BUY_SUCCESS : EventConsts.ITEM_BUY_FAILED;

        EventDto purchasedItemEvent = EventDto.builder().
                gameExternalId(gameState.getGameId()).
                eventExternalId(item.getItemId()).
                gameLives(gameState.getLives()).
                responseLives(itemPurchased.getLives()).
                gameScore(gameState.getScore()).
                responseScore(itemPurchased.getScore()).
                gameLevel(gameState.getLevel()).
                responseLevel(itemPurchased.getLevel()).
                gameGold(gameState.getGold()).
                responseGold(itemPurchased.getGold()).
                gameTurn(gameState.getTurn()).
                responseTurn(itemPurchased.getTurn()).
                operationCounter(gameState.getOperationCounter()).
                operationAmount(cost).
                highScore(itemPurchased.getHighScore()).
                missionBatchCounter(gameState.getMissionBatchCounter()).
                type(EventType.PURCHASE_ITEM).
                outcome(outcome).
                build();
        return saveEvent(purchasedItemEvent);
    }

    public EventDto saveEndGameEvent(GameDto endGameState) {

        EventDto endGameEvent = startEndEventsCommon(endGameState);
        endGameEvent.setOperationCounter(EventConsts.LAST_OPERATION);
        endGameEvent.setType(EventType.GAME_OVER);
        endGameEvent.setOutcome(endGameState.getOutcome().name());
        return saveEvent(endGameEvent);
    }

    public EventDto saveMissionOutOfSynchEvent(GameDto currentState, MissionDto mission,MissionResultDto missionResult) {
        EventDto skippedMissionEvent = missionSkippedCommon(currentState, mission,missionResult);
        skippedMissionEvent.setType(EventType.SKIPPED_MISSION);
        skippedMissionEvent.setOutcome(EventConsts.MISSION_OUT_OF_SYNCH);
        return saveEvent(skippedMissionEvent);
    }

    public EventDto saveMissionNotFoundOnServerEvent(GameDto currentState, MissionDto mission) {
        EventDto skippedMissionEvent = missionSkippedCommon(currentState, mission);
        skippedMissionEvent.setType(EventType.SKIPPED_MISSION);
        skippedMissionEvent.setOutcome(EventConsts.MISSION_NOT_FOUND);
        return saveEvent(skippedMissionEvent);
    }

    public EventDto saveExpiredMissionEvent(GameDto currentState, MissionDto mission) {
        EventDto skippedMissionEvent = missionSkippedCommon(currentState, mission);
        skippedMissionEvent.setType(EventType.SKIPPED_MISSION);
        skippedMissionEvent.setOutcome(EventConsts.MISSION_EXPIRED);
        return saveEvent(skippedMissionEvent);
    }

    @Override
    public EventDto saveDuplicatedMissionEvent(GameDto currentGame, MissionDto mission) {
        EventDto skippedMissionEvent = missionSkippedCommon(currentGame, mission);
        skippedMissionEvent.setType(EventType.SKIPPED_MISSION);
        skippedMissionEvent.setOutcome(EventConsts.MISSION_DUPLICATED);
        return saveEvent(skippedMissionEvent);
    }


    private EventDto startEndEventsCommon(GameDto gameState) {
        return EventDto.builder().
                gameExternalId(gameState.getGameId()).
                eventExternalId(gameState.getGameId()).
                missionBatchCounter(gameState.getMissionBatchCounter()).
                gameLives(gameState.getLives()).
                responseLives(gameState.getLives()).
                gameScore(gameState.getScore()).
                responseScore(gameState.getScore()).
                gameLevel(gameState.getLevel()).
                responseLevel(gameState.getLevel()).
                gameGold(gameState.getGold()).
                responseGold(gameState.getGold()).
                gameTurn(gameState.getTurn()).
                responseTurn(gameState.getTurn()).
                operationAmount(0).
                highScore(gameState.getHighScore()).
                build();
    }

    private EventDto missionSkippedCommon(GameDto currentState, MissionDto mission) {
        return EventDto.builder().
                gameExternalId(currentState.getGameId()).
                eventExternalId(mission.getAdId()).
                missionBatchCounter(currentState.getMissionBatchCounter()).
                gameLives(currentState.getLives()).
                responseLives(currentState.getLives()).
                gameScore(currentState.getScore()).
                responseScore(currentState.getScore()).
                gameLevel(currentState.getLevel()).
                responseLevel(currentState.getLevel()).
                gameGold(currentState.getGold()).
                responseGold(currentState.getGold()).
                gameTurn(currentState.getTurn()).
                responseTurn(currentState.getTurn()).
                operationAmount(mission.getReward()).
                operationCounter(currentState.getOperationCounter()).
                highScore(currentState.getHighScore()).
                build();
    }

    private EventDto missionSkippedCommon(GameDto currentState, MissionDto mission, MissionResultDto missionResult) {
        EventDto event = missionSkippedCommon(currentState, mission);
        event.setResponseGold(missionResult.getGold());
        event.setResponseScore(missionResult.getScore());
        event.setResponseTurn(missionResult.getTurn());
        return event;
    }

    public EventDto saveEvent(EventDto event) {
        EventDto eventSaved = eventMapper.toDto(eventRepository.save(eventMapper.toEntity(event)));
        log.info("Event saved {}", eventSaved);
        return eventSaved;
    }


}
