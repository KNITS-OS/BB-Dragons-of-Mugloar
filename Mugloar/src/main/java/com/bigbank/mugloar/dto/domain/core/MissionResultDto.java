package com.bigbank.mugloar.dto.domain.core;

import com.bigbank.mugloar.model.MissionResultType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MissionResultDto  {

    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private boolean success;
    private String message;
    private MissionResultType outcome;

    private MissionDto mission;
    private GameDto game;
}
