package com.bigbank.mugloar.dto.domain.game;

import com.bigbank.mugloar.dto.domain.core.GameDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameExecutionDto {

    private String gameId;
    private GameDto game;
    private List<EventDto> events;
}
