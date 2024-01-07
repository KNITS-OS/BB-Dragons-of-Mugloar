package com.bigbank.mugloar.dto.api.game.queries;

import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.game.GameExecutionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameExecutionResponse {
    private String gameId;
    private GameDto game;
    List<EventDto> events;

    public GameExecutionResponse (GameExecutionDto execution){
        this.events=execution.getEvents();
        this.gameId=execution.getGameId();
        this.game=execution.getGame();
    }

    public GameExecutionDto toDto(){
        return GameExecutionDto.builder().game(game).gameId(gameId).events(events).build();
    }

}
