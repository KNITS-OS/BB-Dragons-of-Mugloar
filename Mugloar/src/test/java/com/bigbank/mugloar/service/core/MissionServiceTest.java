package com.bigbank.mugloar.service.core;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.dto.domain.game.MissionBatchDto;
import com.bigbank.mugloar.mappers.MissionMapper;
import com.bigbank.mugloar.mock.core.MissionDtoMock;
import com.bigbank.mugloar.model.Mission;
import com.bigbank.mugloar.proxy.MissionProxy;
import com.bigbank.mugloar.repository.core.MissionRepository;
import com.bigbank.mugloar.service.game.GameStateService;
import com.bigbank.mugloar.util.MissionConsts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MissionServiceTest {

    private static final int SAFE_MISSIONS=5;
    private static final int EASY_MISSIONS=5;
    private static final int RISKY_MISSIONS=5;

    private static final int DANGEROUS_MISSIONS=3;

    @Mock
    private MissionProxy missionProxy;

    @Spy
    private MissionMapper missionMapper;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private ApplicationProperties appConfig;

    @Mock
    private ApplicationProperties.Throttling throttling;

    @Mock
    private GameStateService gameStateService;

    @InjectMocks
    private MissionService missionService;


    @Test
    public void testLoadNewMissionBatch() {
        // Given
        String gameId = "123";
        List<MissionDto> availableMissions = availableMissions();
        when(missionProxy.getGameAvailableMissions(gameId)).thenReturn(availableMissions);
        when(gameStateService.getCurrentGameById(gameId)).thenReturn(new GameDto());

        MissionBatchDto result = missionService.loadNewMissionBatch(gameId);

        int allMissions=SAFE_MISSIONS+EASY_MISSIONS+RISKY_MISSIONS+DANGEROUS_MISSIONS;
        assertThat(result).isNotNull();
        assertThat(result.getSafeMissions().size()).isEqualTo(SAFE_MISSIONS);
        assertThat(result.getEasyMissions().size()).isEqualTo(EASY_MISSIONS);
        assertThat(result.getRiskyMissions().size()).isEqualTo(RISKY_MISSIONS);
        assertThat(result.getAvailableMissions().size()).isEqualTo(allMissions);

        assertThat(result.getExecutedMissions().size()).isEqualTo(0);
        assertThat(result.getExecutedMissionResults().size()).isEqualTo(0);
        assertThat(result.getFailedMissions().size()).isEqualTo(0);
        assertThat(result.getFailedMissionsResults().size()).isEqualTo(0);
        assertThat(result.getSkippedMissions().size()).isEqualTo(0);
        assertThat(result.getSkippedMissionsResults().size()).isEqualTo(0);
    }

    private List<MissionDto> availableMissions() {
        List<MissionDto> availableMissions = new ArrayList<>();
        availableMissions.addAll(mockEasyMissions());
        availableMissions.addAll(mockSafeMissions());
        availableMissions.addAll(mockRiskyMissions());
        availableMissions.addAll(mockDangerousMissions());
        return availableMissions;
    }

    @Test
    public void testExecuteMission() {
        // Given
        String gameId = "123";
        int gameTurn=1;
        int responseTurn=gameTurn+1;

        GameDto currentGame =GameDto.builder().gameId(gameId).turn(gameTurn).build();
        MissionDto currentMission = MissionDto.builder().adId("AdId1").game(currentGame).build();
        MissionResultDto missionResultDto = MissionResultDto.builder().turn(responseTurn).build();

        when(gameStateService.getCurrentGameById(gameId)).thenReturn(currentGame);
        when(missionProxy.takeMission(gameId, currentMission.getAdId())).thenReturn(missionResultDto);
        when(throttling.getApiThrottlingDelay()).thenReturn(2L);
        when(appConfig.getThrottling()).thenReturn(throttling);

        // When
        MissionResultDto result = missionService.executeMission(gameId, currentMission);

        // Then
        verify(gameStateService, times(1)).getCurrentGameById(gameId);
        verify(missionProxy, times(1)).takeMission(gameId, currentMission.getAdId());
        verify(throttling, times(1)).getApiThrottlingDelay();
        verify(appConfig, times(1)).getThrottling();
        assertThat(result).isNotNull();

    }



    @Test
    public void testSaveAll() {
        // Given
        List<MissionDto> missionDtos = Arrays.asList(new MissionDto(), new MissionDto());
        List<Mission> missionEntities = Arrays.asList(new Mission(), new Mission());
        when(missionRepository.saveAll(any())).thenReturn(missionEntities);
        when(missionMapper.toDtos(missionEntities)).thenReturn(missionDtos);

        // When
        List<MissionDto> result = missionService.saveAll(missionDtos);

        // Then
        verify(missionMapper, times(1)).toEntities(any());
        verify(missionRepository, times(1)).saveAll(any());
        verify(missionMapper, times(1)).toDtos(any());
        assertThat(result.size()).isEqualTo(missionDtos.size());

    }

    private List<MissionDto> mockSafeMissions (){
        return MissionDtoMock.shallowMissionDtos(SAFE_MISSIONS, MissionConsts.SURE_THING);
    }

    private List<MissionDto> mockEasyMissions (){
        return MissionDtoMock.shallowMissionDtos(SAFE_MISSIONS,EASY_MISSIONS, MissionConsts.WALK_IN_THE_PARK);
    }

    private List<MissionDto> mockRiskyMissions (){
        int easyMissions=SAFE_MISSIONS+EASY_MISSIONS;
        return MissionDtoMock.shallowMissionDtos(easyMissions,SAFE_MISSIONS, MissionConsts.GAMBLE);
    }

    private List<MissionDto> mockDangerousMissions (){
        int riskyMissions=SAFE_MISSIONS+EASY_MISSIONS+RISKY_MISSIONS;
        return MissionDtoMock.shallowMissionDtos(riskyMissions,DANGEROUS_MISSIONS, MissionConsts.IMPOSSIBLE);
    }
}
