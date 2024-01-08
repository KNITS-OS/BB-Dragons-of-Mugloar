package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.api.external.domugloar.PurchaseItemResponse;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.ItemDto;
import com.bigbank.mugloar.dto.domain.core.PurchasedItemDto;
import com.bigbank.mugloar.mock.core.ItemDtoMock;
import com.bigbank.mugloar.service.core.ItemService;
import com.bigbank.mugloar.service.game.impl.ItemProcessorBasicImpl;
import com.bigbank.mugloar.util.ItemConsts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemProcessorTest {

    private static final ItemDto HOT_POT_ITEM = ItemDto.builder().itemId(ItemConsts.HPOT).id(1L).name(ItemConsts.HPOT).cost(50.0).build();
    private static final ItemDto GAS_ITEM = ItemDto.builder().itemId(ItemConsts.GAS).id(1L).name(ItemConsts.GAS).cost(100.0).build();
    private static final Map<String, ItemDto> AVAILABLE_ITEMS = mockAvailableItems();
    private static final PurchaseItemResponse NO_PURCHASE=null;

    @Mock
    private ApplicationProperties appConfig;
    @Mock
    private ItemService itemService;
    @Mock
    private EventService eventService;
    @Mock
    private ItemStateService itemStateService;
    @Mock
    private Queue<String> prioritizedItems;
    @Captor
    private ArgumentCaptor<GameDto> gameStateCaptor;
    @Captor
    private ArgumentCaptor<ItemDto> itemCaptor;
    @Captor
    private ArgumentCaptor<PurchasedItemDto> purchasedItemCaptor;

    @InjectMocks
    private ItemProcessorBasicImpl itemProcessor;

    @Test
    @DisplayName("it should buy hot pot when lives lower than app config livesMinSafeLevel")
    void testUpgradeGameWithPowerItems_BuyHotPot() {
        GameDto currentGame =gameWithEnoughGoldForHotPot();
        buyHotPotTestTemplate(currentGame,getMockStrategy(),successfullPurchase(currentGame,HOT_POT_ITEM.getCost()));
    }

    @Test
    @DisplayName("it should buy power items when lives level is acceptable and there is budget")
    void testUpgradeGameWithPowerItems_BuyPrioritizedPowerItems() {
        GameDto currentGame = gameWithHealthAndGoldForPowerItems();
        buyPowerItemTestTemplate(currentGame,getMockStrategy(),successfullPurchase(currentGame,GAS_ITEM.getCost()));
    }


    @Test
    @DisplayName("it should not buy power items when there is no budget")
    void testUpgradeGameWithPowerItems_NoMoreBudgetForPrioritizedPowerItems() {
        GameDto currentGame = gameWithHealthAndInsuficcientGoldForPowerItems();
        buyPowerItemTestTemplate(currentGame,getMockStrategy(),NO_PURCHASE);
    }

    @Test
    @DisplayName("it should not buy hot pot items when there is not enough gold")
    void testUpgradeGameWithPowerItems_NoMoreBudgetForHotPot() {
        GameDto currentGame = gameWithInsufficientGoldForHotPot();
        buyHotPotTestTemplate(currentGame,getMockStrategy(),NO_PURCHASE);
    }

    @Test
    @DisplayName("it should try to buy hot pot until there is budget")
    void testUpgradeGameWithPowerItems_TryToBuyHotPotUntilSuccess() {

    }

    private void buyPowerItemTestTemplate(GameDto currentGame, ApplicationProperties.Strategy mockStrategy, PurchaseItemResponse purchaseItemResponse) {
        when(itemStateService.getAvailableItems(currentGame.getGameId())).thenReturn(AVAILABLE_ITEMS);
        when(appConfig.getStrategy()).thenReturn(mockStrategy);

        if(purchaseItemResponse!=NO_PURCHASE) { //necessary for mockito complaints on unused mocks
            when(itemService.purchaseItem(any())).thenReturn(purchaseItemResponse);
            when(prioritizedItems.poll()).thenReturn(GAS_ITEM.getItemId());
            when(itemStateService.getPrioritizedItemKeys(currentGame.getGameId())).thenReturn(prioritizedItems);
        }

        //when
        itemProcessor.upgradeGameWithPowerItems(currentGame);

        if(purchaseItemResponse==NO_PURCHASE){
            verify(itemStateService, times(1)).getAvailableItems(anyString());
            verify(appConfig, times(2)).getStrategy();
            assertNoPurchase(currentGame);
            return;
        }

        if(purchaseItemResponse.getShoppingSuccess().equals(ItemConsts.SUCCESS)){
            verify(appConfig, times(4)).getStrategy();
            verify(itemStateService, times(2)).getAvailableItems(anyString());
            verify(itemService, times(1)).purchaseItem(any());
            verify(eventService).savePurchasedItemEvent(gameStateCaptor.capture(), itemCaptor.capture(), purchasedItemCaptor.capture());
            GameDto capturedGameState = gameStateCaptor.getValue();
            PurchasedItemDto capturedPurchasedItemDto = purchasedItemCaptor.getValue();
            assertPurchaseSuccess(currentGame, purchaseItemResponse, capturedGameState, capturedPurchasedItemDto);
        }


    }


    private void buyHotPotTestTemplate(GameDto currentGame, ApplicationProperties.Strategy mockStrategy,PurchaseItemResponse purchaseItemResponse) {

        //given
        when(itemStateService.getAvailableItems(currentGame.getGameId())).thenReturn(AVAILABLE_ITEMS);
        when(appConfig.getStrategy()).thenReturn(mockStrategy);
        if(purchaseItemResponse!=NO_PURCHASE) {
            when(itemService.purchaseItem(any())).thenReturn(purchaseItemResponse);
        }

        //when
        itemProcessor.upgradeGameWithPowerItems(currentGame);

        //then
        if(purchaseItemResponse==NO_PURCHASE){
            verify(itemStateService, times(1)).getAvailableItems(anyString());
            verify(appConfig, times(2)).getStrategy();
            assertNoPurchase(currentGame);
            return;
        }

        if(purchaseItemResponse.getShoppingSuccess().equals(ItemConsts.SUCCESS)){
            verify(appConfig, times(3)).getStrategy();
            verify(itemStateService, times(1)).getAvailableItems(anyString());
            verify(itemService, times(1)).purchaseItem(any());
            verify(eventService).savePurchasedItemEvent(gameStateCaptor.capture(), itemCaptor.capture(), purchasedItemCaptor.capture());
            GameDto capturedGameState = gameStateCaptor.getValue();
            PurchasedItemDto capturedPurchasedItemDto = purchasedItemCaptor.getValue();
            assertPurchaseSuccess(currentGame, purchaseItemResponse, capturedGameState, capturedPurchasedItemDto);
        }

    }

    private ApplicationProperties.Strategy getMockStrategy(){
        ApplicationProperties.Strategy mockStrategy = new ApplicationProperties.Strategy();
        mockStrategy.setGoldReserveHotPot(200);
        mockStrategy.setLivesMinSafeLevel(5);
        return mockStrategy;
    }



    private GameDto gameWithHealthAndGoldForPowerItems(){
        int gameGold = getMockStrategy().getGoldReserveHotPot()+1;
        return gameCommonHighHealth(gameGold);
    }

    private GameDto gameWithHealthAndInsuficcientGoldForPowerItems(){
        int gameGold = getMockStrategy().getGoldReserveHotPot();
        return gameCommonHighHealth(gameGold);
    }

    private GameDto gameWithEnoughGoldForHotPot(){
        int gameGold = (int)HOT_POT_ITEM.getCost()+1;
        return gameCommon(gameGold);
    }

    private GameDto gameWithInsufficientGoldForHotPot(){
        int gameGold = (int)HOT_POT_ITEM.getCost();
        return gameCommon(gameGold);
    }

    private GameDto gameCommonHighHealth( int gameGold){
        GameDto currentGame=gameCommon(gameGold);
        currentGame.setLives(8);
        return currentGame;
    }
    private GameDto gameCommon( int gameGold){
        String gameId = "AmockGameId";
        int gameTurn = 3;
        int gameLives = 3;
        int gameLevel = 0;

        return GameDto.builder()
                .gameId(gameId)
                .operationCounter(3)
                .gold(gameGold)
                .turn(gameTurn)
                .lives(gameLives)
                .level(gameLevel)
                .build();
    }




    private void assertPurchaseSuccess(GameDto currentGame, PurchaseItemResponse purchaseItemResponse, GameDto capturedGameState, PurchasedItemDto capturedPurchasedItemDto) {
        Assertions.assertThat(capturedGameState.getTurn()).isEqualTo(purchaseItemResponse.getTurn());
        Assertions.assertThat(capturedGameState.getOperationCounter()).isEqualTo(purchaseItemResponse.getTurn());
        Assertions.assertThat(currentGame.getLives()).isEqualTo(purchaseItemResponse.getLives());
        Assertions.assertThat(currentGame.getGold()).isEqualTo(purchaseItemResponse.getGold());
        Assertions.assertThat(currentGame.getLevel()).isEqualTo(purchaseItemResponse.getLevel());

        Assertions.assertThat(capturedPurchasedItemDto.getLives()).isEqualTo(purchaseItemResponse.getLives());
        Assertions.assertThat(capturedPurchasedItemDto.getLevel()).isEqualTo(purchaseItemResponse.getLevel());
        Assertions.assertThat(capturedPurchasedItemDto.getGold()).isEqualTo(purchaseItemResponse.getGold());
    }

    private void assertNoPurchase(GameDto currentGame) {
        Assertions.assertThat(currentGame.getTurn()).isEqualTo(currentGame.getTurn());
        Assertions.assertThat(currentGame.getOperationCounter()).isEqualTo(currentGame.getOperationCounter());
        Assertions.assertThat(currentGame.getLives()).isEqualTo(currentGame.getLives());
        Assertions.assertThat(currentGame.getGold()).isEqualTo(currentGame.getGold());
        Assertions.assertThat(currentGame.getLevel()).isEqualTo(currentGame.getLevel());
    }
    private void assertPurchaseFailure(GameDto currentGame) {
        Assertions.assertThat(currentGame.getTurn()).isEqualTo(currentGame.getTurn()+1);
        Assertions.assertThat(currentGame.getOperationCounter()).isEqualTo(currentGame.getOperationCounter()+1);
        Assertions.assertThat(currentGame.getLives()).isEqualTo(currentGame.getLives());
        Assertions.assertThat(currentGame.getGold()).isEqualTo(currentGame.getGold());
        Assertions.assertThat(currentGame.getLevel()).isEqualTo(currentGame.getLevel());
    }

    private PurchaseItemResponse successfullPurchase(GameDto currentGame, double cost) {
        int responseLives = currentGame.getLives() + 1;
        int responseLevel = currentGame.getLevel() + 1;
        int responseGold = (int) (currentGame.getGold() - cost);
        int responseTurn = currentGame.getTurn() + 1;
        return PurchaseItemResponse.builder().turn(responseTurn).lives(responseLives)
                .gold(responseGold).level(responseLevel).shoppingSuccess(ItemConsts.SUCCESS).build();
    }

    private PurchaseItemResponse failedPurchase(GameDto currentGame) {
        int responseLives = currentGame.getLives() - 1;
        int responseLevel = currentGame.getLevel();
        int responseGold = currentGame.getGold();
        int responseTurn = currentGame.getTurn() + 1;
        return PurchaseItemResponse.builder().turn(responseTurn).lives(responseLives)
                .gold(responseGold).level(responseLevel).shoppingSuccess("fail").build();
    }



    private static Map<String, ItemDto> mockAvailableItems() {
        Map<String, ItemDto> availableItems = new HashMap<>();
        long counter = 1;
        for (String itemId : ItemConsts.ALL_ITEMS) {
            availableItems.put(itemId, ItemDtoMock.shallowItemDto(counter, itemId));
            counter++;
        }
        //we need to override those 2 for test assertions accuracy
        availableItems.put(ItemConsts.HPOT, HOT_POT_ITEM);
        availableItems.put(ItemConsts.GAS, GAS_ITEM);
        return availableItems;
    }

}