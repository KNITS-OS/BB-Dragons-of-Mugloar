package com.bigbank.mugloar.service.game.impl;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemRequest;
import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemResponse;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.ItemDto;
import com.bigbank.mugloar.dto.domain.core.PurchasedItemDto;
import com.bigbank.mugloar.processor.ItemProcessor;
import com.bigbank.mugloar.service.core.ItemService;
import com.bigbank.mugloar.service.game.EventService;
import com.bigbank.mugloar.service.game.ItemStateService;
import com.bigbank.mugloar.util.ItemConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemProcessorBasicImpl implements ItemProcessor {

    private final ApplicationProperties appConfig;
    private final ItemService itemService;
    private final EventService eventService;
    private final ItemStateService itemStateService;

    @Override
    public void upgradeGameWithPowerItems(GameDto currentGame) {
        evaluateHealingWithHotPot(currentGame);
        evaluateBuyPowerItems100Gold(currentGame);
    }

    private void evaluateHealingWithHotPot(GameDto gameState) {

        ItemDto hotPotItem= itemStateService.getAvailableItems(gameState.getGameId()).get(ItemConsts.HPOT);
        while (gameState.getLives() < appConfig.getStrategy().getLivesMinSafeLevel() &&
                gameState.getGold() > hotPotItem.getCost()) {
             buyPowerItem(gameState, hotPotItem);
        }
    }


    private void evaluateBuyPowerItems100Gold(GameDto gameState) {

        while (gameState.getGold() > appConfig.getStrategy().getGoldReserveHotPot() &&
                gameState.getLives() >= appConfig.getStrategy().getLivesMinSafeLevel()) {
            String itemId = pollNextItemId(gameState.getGameId());
            ItemDto itemToBuy = itemStateService.getAvailableItems(gameState.getGameId()).get(itemId);
            buyPowerItem(gameState, itemToBuy);
        }
    }


    private String pollNextItemId(String gameId){
        Queue<String> prioritizedItems =itemStateService.getPrioritizedItemKeys(gameId);
        String itemId=itemStateService.getPrioritizedItemKeys(gameId).poll();
        if(itemId==null){
            prioritizedItems.addAll(new LinkedList<>(ItemConsts.POWER_ITEMS_100));
            itemStateService.reloadPrioritizedItemKeys(gameId,prioritizedItems);
            itemId=prioritizedItems.poll();
        }
        return itemId;
    }

    private void buyPowerItem(GameDto gameState, ItemDto itemToBuy) {
        PurchaseItemRequest request = PurchaseItemRequest.builder().gameId(gameState.getGameId()).itemId(itemToBuy.getItemId()).build();
        PurchaseItemResponse response = itemService.purchaseItem(request);
        updateCurrentGameStateWithItems(gameState, itemToBuy, response);
    }

    private void updateCurrentGameStateWithItems(GameDto gameState, ItemDto item, PurchaseItemResponse response) {
        log.info("Purchased {} with Outcome: {} ", item.getItemId(), response);

        if (gameState.getTurn() < response.getTurn()) {

            gameState.setTurn(response.getTurn());
            gameState.increaseOperationCounter();
            eventService.savePurchasedItemEvent(gameState, item, response.toDto());

            gameState.setLives(response.getLives());
            gameState.setGold(response.getGold());
            gameState.setLevel(response.getLevel());

            if (appConfig.isSaveExecution()) {
                savePurchaseItem(gameState, item, response.toDto());
            }

        } else {
            log.warn("Game {} PurchaseItem {} didnt increase Turn current Game {}, Purchase Result {} Event will be not tracked",
                    gameState.getGameId(), item.getItemId(), gameState.getTurn(), response.getTurn());
        }
    }

    private void savePurchaseItem(GameDto gameState, ItemDto itemPurchased, PurchasedItemDto purchasedItem) {
        purchasedItem.setItemDto(itemPurchased);
        purchasedItem.setGame(gameState);
        itemService.savePurchasedItem(purchasedItem);
    }

}
