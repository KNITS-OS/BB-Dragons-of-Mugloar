package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;
import com.bigbank.mugloar.mock.core.MissionDtoMock;
import com.bigbank.mugloar.service.game.impl.MissionEvaluatorBasicImpl;
import com.bigbank.mugloar.util.MissionConsts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static com.bigbank.mugloar.util.MissionConsts.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MissionEvaluatorTest {

    private static final String MOCK_GAME_ID="mock-GameId";
    private static final String MOCK_MISSION_ID="IrrelevantInChecks";

    private static final int SAFE_LIMIT=3;
    private static final int EASY_LIMIT=4;
    private static final int RISKY_LIMIT=5;
    private ApplicationProperties.Strategy mockStrategy =getMockStrategy(SAFE_LIMIT,EASY_LIMIT, RISKY_LIMIT);
    @Mock
    private ApplicationProperties appConfig;

    @Mock
    private GameStateService gameStateService;

    @InjectMocks
    private MissionEvaluatorBasicImpl missionEvaluator;

    @Test
    @DisplayName("it should poll only safe mission if lives under lives-accept-safe-limit")
    void selectNextMissionFromBatch_OnlySafeMissions() {
        int lives=SAFE_LIMIT-1;
        MissionDto selectedMission =evaluatorTestTemplate(lives);
        assertThat(SAFE_MISSIONS.contains(selectedMission.getProbability())).isTrue();
    }

    @Test
    @DisplayName("it should poll higher reward mission between safe and easy according to lives and config")
    void selectNextMissionFromBatch_EasyMissionsByHigherReward() {
        int lives=EASY_LIMIT;
        MissionDto selectedMission =evaluatorTestTemplate(lives);
        assertThat(EASY_MISSIONS.contains(selectedMission.getProbability())).isTrue();

    }

    @Test
    @DisplayName("it should poll higher reward mission for all acceptable missions according to lives and config")
    void selectNextMissionFromBatch_AllMissionsByHigherReward() {
        int lives=RISKY_LIMIT;
        MissionDto selectedMission =evaluatorTestTemplate(lives);
        assertThat(RISKY_MISSIONS.contains(selectedMission.getProbability())).isTrue();
    }

    private MissionDto evaluatorTestTemplate (int lives){
        MissionBatchDto missionBatch = createMissionBatch();
        GameDto gameState = GameDto.builder()
                .gameId(MOCK_GAME_ID)
                .lives(lives)
                .executedMissionsIds(new HashSet<>())
                .build();


        when(gameStateService.getCurrentGameById(any())).thenReturn(gameState);
        when(appConfig.getStrategy()).thenReturn(mockStrategy);

        MissionDto selectedMission = missionEvaluator.selectNextMissionFromBatch(missionBatch);

        assertThat(selectedMission).isNotNull();
        verify(gameStateService, times(1)).getCurrentGameById(any());
        return selectedMission;
    }


    private MissionBatchDto createMissionBatch() {

        Queue<MissionDto> safeMissions =mockMissionsWithWeightedReward(SURE_THING,100);
        Queue<MissionDto> easyMissions =mockMissionsWithWeightedReward(WALK_IN_THE_PARK,101);
        Queue<MissionDto> riskyMissions =mockMissionsWithWeightedReward(GAMBLE,102);
        Queue<MissionDto> dangerousMissions =mockMissionsWithWeightedReward(IMPOSSIBLE,1000000);
        Set<MissionDto> availableMissions = new HashSet<>(safeMissions);
        availableMissions.addAll(easyMissions);
        availableMissions.addAll(riskyMissions);
        availableMissions.addAll(dangerousMissions);

        return MissionBatchDto.builder().
                safeMissions(safeMissions).
                easyMissions(easyMissions).
                riskyMissions(riskyMissions).
                availableMissions(availableMissions).
                build();
    }

    private Queue<MissionDto> mockMissionsWithWeightedReward(String probability,int weightedReward){
        Queue<MissionDto> missionQueue = new LinkedList<>();
        MissionDto mission =MissionDtoMock.shallowMissionDto(MOCK_MISSION_ID,MOCK_GAME_ID);
        mission.setProbability(probability);
        mission.setRiskWeightedReward(weightedReward);
        missionQueue.add(mission);
        return missionQueue;
    }

    private ApplicationProperties.Strategy getMockStrategy(int safeLimit,int easyLimit, int riskyLimit){
        ApplicationProperties.Strategy mockStrategy = new ApplicationProperties.Strategy();
        mockStrategy.setLivesAcceptSafeLimit(safeLimit);
        mockStrategy.setLivesAcceptEasyLimit(easyLimit);
        mockStrategy.setLivesAcceptRiskyLimit(riskyLimit);
        return mockStrategy;
    }
}
