package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.service.game.impl.MissionEvaluatorBasicImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MissionEvaluatorTest {

    @Mock
    private ApplicationProperties appConfig;

    @Mock
    private GameStateService gameStateService;

    @InjectMocks
    private MissionEvaluatorBasicImpl missionEvaluator;

    @Test
    @DisplayName("it should poll only safe mission if lives under lives-accept-safe-limit")
    void selectNextMissionFromBatch_OnlySafeMissions() {

    }

    @Test
    @DisplayName("it should poll higher reward mission between safe and easy according to lives and config")
    void selectNextMissionFromBatch_EasyMissionsByHigherReward() {

    }

    @Test
    @DisplayName("it should poll higher reward mission for all acceptable missions according to lives and config")
    void selectNextMissionFromBatch_AllMissionsByHigherReward() {

    }

    @Test
    @DisplayName("it should filter out all missions above risky probability level")
    void selectNextMissionFromBatch_FilterUnreasonableMissions() {

    }

}
