package com.bigbank.mugloar.dto.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameExecutionReportDto {

    private Set<String> gameIds;
    private String message;
    private int code;
}
