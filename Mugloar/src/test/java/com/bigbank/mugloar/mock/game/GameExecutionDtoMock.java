package com.bigbank.mugloar.mock.game;

import com.bigbank.mugloar.dto.domain.game.GameExecutionDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class GameExecutionDtoMock {

    public static GameExecutionDto shallowGameExecutionDto(String gameId) {
        return GameExecutionDto.builder()
                .gameId(gameId)
                .build();
    }
}
