package com.bigbank.mugloar.dto.api.game.command;

import com.bigbank.mugloar.dto.domain.game.GameExecutionReportDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartGameExecutionResponse {

    private Set<String> gameIds;
    private String message;
    private int code;

    public StartGameExecutionResponse(GameExecutionReportDto report) {
        this.gameIds = report.getGameIds();
        this.message = report.getMessage();
        this.code = report.getCode();
    }

    public GameExecutionReportDto toDto() {
        return GameExecutionReportDto.builder()
                .gameIds(gameIds)
                .message(message)
                .code(code)
                .build();
    }

}
