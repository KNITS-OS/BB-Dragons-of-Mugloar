package com.bigbank.mugloar.util;

import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.model.EventType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventUtils {

    public static EventDto startGame(GameDto gameState) {

        return EventDto.builder().
                gameExternalId(gameState.getGameId()).
                eventExternalId(gameState.getGameId()).
                missionBatchCounter(gameState.getMissionBatchCounter()).
                gameLives(gameState.getLives()).
                gameScore(gameState.getScore()).
                gameLevel(gameState.getLevel()).
                gameGold(gameState.getGold()).
                gameTurn(gameState.getTurn()).
                highScore(gameState.getHighScore()).
                operationAmount(0).
                operationCounter(gameState.getOperationCounter()).
                type(EventType.GAME_START).
                outcome("Game Started").
                build();
    }

    public static EventDto executedMission(GameDto gameState, MissionDto mission, MissionResultDto missionResult) {

        if (missionResult.getScore() != gameState.getScore()) {
            log.warn("Game and Mission result have inconsistent values: Game Score {} Mission Result {} ", gameState.getScore(), missionResult.getScore());
        }
        String outcome =missionResult.isSuccess()? "Success" : "Failed";

        return EventDto.builder().
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
    }

//    public static EventDto purchasedItem(GameStateDto gameState, ItemDto item, PurchasedItemDto itemPurchased) {
//
//        if (itemPurchased.getScore() != gameState.getSummary().getScore()) {
//            log.warn("Game and Item Purchase result have inconsistent values: Game Score {} Mission Result {} ", gameState.getSummary().getScore(), itemPurchased.getScore());
//        }
//        int cost =(int)(item.getCost()* -1);
//
//        return EventDto.builder().
//                gameExternalId(gameState.getSummary().getGameId()).
//                eventExternalId(item.getItemId()).
//                gameLives(gameState.getSummary().getLives()).
//                responseLives(itemPurchased.getLives()).
//                gameScore(gameState.getSummary().getScore()).
//                responseScore(itemPurchased.getScore()).
//                gameLevel(gameState.getSummary().getLevel()).
//                responseLevel(itemPurchased.getLevel()).
//                gameGold(gameState.getSummary().getGold()).
//                responseGold(itemPurchased.getGold()).
//                gameTurn(gameState.getSummary().getTurn()).
//                responseTurn(itemPurchased.getTurn()).
//                operationCounter(gameState.getSummary().getOperationCounter()).
//                operationAmount(cost).
//                highScore(itemPurchased.getHighScore()).
//                missionBatchCounter(gameState.getMissionBatch().getMissionBatchCounter()).
//                type(EventType.PURCHASE_ITEM).
//                outcome(itemPurchased.getShoppingSuccess()).
//                build();
//    }

//    public static EventDto endGame(GameStateDto endGameState) {
//
//        return EventDto.builder().
//                gameExternalId(endGameState.getGameId()).
//                eventExternalId(endGameState.getGameId()).
//                missionBatchCounter(endGameState.getMissionBatch().getMissionBatchCounter()).
//                gameLives(endGameState.getSummary().getLives()).
//                responseLives(Consts.MISSING_RESPONSE_VALUE).
//                gameScore(endGameState.getSummary().getScore()).
//                responseScore(Consts.MISSING_RESPONSE_VALUE).
//                gameLevel(endGameState.getSummary().getLevel()).
//                responseLevel(Consts.MISSING_RESPONSE_VALUE).
//                gameGold(endGameState.getSummary().getGold()).
//                responseGold(Consts.MISSING_RESPONSE_VALUE).
//                gameTurn(endGameState.getSummary().getTurn()).
//                responseTurn(Consts.END_GAME_REPONSE_VALUE).
//                operationAmount(0).
//                operationCounter(1000).
//                highScore(endGameState.getSummary().getHighScore()).
//                type(EventType.GAME_END).
//                outcome(endGameState.getSummary().getOutcome().name()).
//                build();
//    }


}
