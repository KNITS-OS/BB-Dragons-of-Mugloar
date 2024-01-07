package com.bigbank.mugloar.dto.domain.core;

import com.bigbank.mugloar.model.GameStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {

    private Long id;
    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int turn;
    private int highScore;

    private int operationCounter;
    private int missionBatchCounter;
    private int missionExecutedCounter;
    private int missionFailedCounter;
    private int notFoundBatchReloadCounter;
    private int expiredMissionsCounter;
    private int oosMissionsCounter;
    private int notFoundMissionsCounter;
    private int potentialScore;


    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GameStateType outcome;


    private Set<String> executedMissionsIds;

    public GameDto(String gameId) {
        this.gameId = gameId;
        this.gold = 0;
        this.lives = 3;
        this.level = 0;
        this.score = 0;
        this.highScore = 0;
        this.turn = 0;
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        this.outcome = null;
    }


    public boolean isStillAlive() {
        return lives > 0;
    }

    public boolean isVictory() {
        return score > 1000;
    }


    public void gameOver() {
        setEndTime(LocalDateTime.now());
        GameStateType gameFinalState = isVictory() ? GameStateType.VICTORY : GameStateType.DEFEAT;
        setOutcome(gameFinalState);
    }

    public boolean isRunning() {
        return !isVictory() && isStillAlive();
    }

    public void increaseOperationCounter() {
        operationCounter++;
    }

    public void increaseMissionBatchCounter() {
        missionBatchCounter++;
    }

    public void increaseNotFoundBatchReloadCounter() {
        notFoundBatchReloadCounter++;
    }
    public void increaseMissionsNotFoundCounter() {
        notFoundMissionsCounter++;
    }
     public void increaseMissionsOutOfSyncCounter() {
        oosMissionsCounter++;
    }
    public void increaseMissionExecutedCounter() {
        missionExecutedCounter++;
    }
    public void increaseMissionFailedCounter() {
        missionFailedCounter++;
    }
    public void increaseMissionExpiredCounter() {
        expiredMissionsCounter++;
    }

    public void increasePotentialScore (int lastPotentialScore) {
        potentialScore+=lastPotentialScore;
    }




}
