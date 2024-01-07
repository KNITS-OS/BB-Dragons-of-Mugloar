package com.bigbank.mugloar.mock.core;

import com.bigbank.mugloar.dto.domain.core.GameDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;


@Data
@NoArgsConstructor
@Builder
public class GameDtoMock {

    public static GameDto shallowGameDto(Long id, String gameId) {
        return GameDto.builder()
                .id(id)
                .gameId(gameId)
                .lives(3)
                .gold(100)
                .level(5)
                .score(2000)
                .turn(10)
                .highScore(2500)
                .operationCounter(42)
                .missionBatchCounter(5)
                .missionExecutedCounter(3)
                .missionFailedCounter(1)
                .notFoundBatchReloadCounter(2)
                .expiredMissionsCounter(1)
                .oosMissionsCounter(1)
                .notFoundMissionsCounter(1)
                .potentialScore(500)
                .startTime(LocalDateTime.now())
                .endTime(null)
                .outcome(null)
                .executedMissionsIds(new HashSet<>())
                .build();
    }
}
