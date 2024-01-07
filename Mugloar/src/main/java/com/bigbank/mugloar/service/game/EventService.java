package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.dto.domain.core.*;

import java.util.List;


public interface EventService {
    List<EventDto> findEventsByGameId(String gameId);
    EventDto saveStartGameEvent(GameDto gameState);
    EventDto saveExecutedMissionEvent(GameDto gameState, MissionDto mission, MissionResultDto missionResult);
    EventDto savePurchasedItemEvent(GameDto gameState, ItemDto item, PurchasedItemDto itemPurchased);
    EventDto saveEndGameEvent(GameDto endGameState);
    EventDto saveMissionOutOfSynchEvent(GameDto currentState, MissionDto mission, MissionResultDto missionResult);
    EventDto saveMissionNotFoundOnServerEvent(GameDto currentState, MissionDto mission);
    EventDto saveExpiredMissionEvent(GameDto currentState, MissionDto mission) ;

    EventDto saveDuplicatedMissionEvent(GameDto currentGame, MissionDto mission);
}
