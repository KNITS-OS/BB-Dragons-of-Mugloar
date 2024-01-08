package com.bigbank.mugloar.mock.dto.core;

import com.bigbank.mugloar.dto.domain.core.GameDto;

import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


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

    public static List<GameDto> shallowGameDtos(int howMany) {
        List<GameDto> games = new ArrayList<>();
        for (long id = 1; id <= howMany; id++) {
            games.add(shallowGameDto(id, "GameId"+id));
        }
        return games;
    }
}
