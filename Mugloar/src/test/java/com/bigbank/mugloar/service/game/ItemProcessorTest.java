package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.service.core.ItemService;
import com.bigbank.mugloar.service.game.impl.ItemProcessorBasicImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ItemProcessorTest {
    @Mock
    private ApplicationProperties appConfig;
    @Mock
    private ItemService itemService;
    @Mock
    private EventService eventService;

    @Mock
    private ItemStateService itemStateService;

    @InjectMocks
    private ItemProcessorBasicImpl itemProcessor;


    @Test
    @DisplayName("it should buy hot pot when lives lower than app config livesMinSafeLevel")
    void testUpgradeGameWithPowerItems_BuyHotPot() {

    }

    @Test
    @DisplayName("it should buy power items when lives level is acceptable and there is budget")
    void testUpgradeGameWithPowerItems_BuyPrioritizedPowerItems() {

    }


    @Test
    @DisplayName("it should not buy power items when there is no budget")
    void testUpgradeGameWithPowerItems_NoMoreBudgetForPrioritizedPowerItems() {

    }

    @Test
    @DisplayName("it should not buy hot pot items when there is no budget")
    void testUpgradeGameWithPowerItems_NoMoreBudgetForHotPot() {

    }

    @Test
    @DisplayName("it should try to buy hot pot until there is budget")
    void testUpgradeGameWithPowerItems_TryToBuyHotPotUntilSuccess() {

    }


}