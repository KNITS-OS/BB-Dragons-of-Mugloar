package com.bigbank.mugloar.mock.core;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.model.MissionOutcomeType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class MissionDtoMock {

    public static MissionDto shallowMissionDto(String adId, String gameExternalId) {
        return MissionDto.builder()
                .adId(adId)
                .gameExternalId(gameExternalId)
                .message("MockMessage")
                .reward(100)
                .expiresIn(5)
                .encrypted(null)
                .probability("50%")
                .riskWeightedReward(120)
                .outcome(MissionOutcomeType.SUCCESS)
                .retryCount(3)
                .game(GameDtoMock.shallowGameDto(1L, "MockGame"))
                .build();
    }
    public static List<MissionDto> shallowMissionDtos(int howMany, String probability) {
       return shallowMissionDtos(0,howMany,probability);
    }

    public static List<MissionDto> shallowMissionDtos(int startIndex, int howMany, String probability) {
        List<MissionDto> missions = new ArrayList<>();
        for (int i=startIndex;i<howMany+startIndex; i++){
            MissionDto mockMission =shallowMissionDto("mid"+i, "gameId"+i);
            mockMission.setProbability(probability);
            missions.add(mockMission);
        }
        return missions;
    }

}
