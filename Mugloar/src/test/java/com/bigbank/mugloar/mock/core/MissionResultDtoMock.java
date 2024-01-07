package com.bigbank.mugloar.mock.core;

import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.model.MissionResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class MissionResultDtoMock {

    public static MissionResultDto shallowMissionResultDto(boolean success) {
        return MissionResultDto.builder()
                .lives(2)
                .gold(50)
                .score(1500)
                .highScore(2000)
                .turn(8)
                .success(success)
                .message("MockMessage")
                .outcome(MissionResultType.SUCCESS)
                .build();
    }
}
