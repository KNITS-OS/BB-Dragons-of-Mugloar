package com.bigbank.mugloar.mock.core;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.model.MissionOutcomeType;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class MissionDtoMock {

    public static MissionDto shallowMissionDto(String adId, String gameExternalId) {
        return MissionDto.builder()
                .adId(adId)
                .gameExternalId(gameExternalId)
                .message("MockMessage")
                .reward(100)
                .expiresIn(5)
                .encrypted("MockEncryption")
                .probability("50%")
                .riskWeightedReward(120)
                .outcome(MissionOutcomeType.SUCCESS)
                .retryCount(3)
                .game(GameDtoMock.shallowGameDto(1L, "MockGame"))
                .build();
    }
}
