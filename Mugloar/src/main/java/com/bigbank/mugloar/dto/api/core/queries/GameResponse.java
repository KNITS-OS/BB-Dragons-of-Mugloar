package com.bigbank.mugloar.dto.api.core.queries;

import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.model.GameStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponse {

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


    public GameResponse (GameDto gameDto) {
        this.id = gameDto.getId();
        this.gameId = gameDto.getGameId();
        this.lives = gameDto.getLives();
        this.gold = gameDto.getGold();
        this.level = gameDto.getLevel();
        this.score = gameDto.getScore();
        this.turn = gameDto.getTurn();
        this.highScore = gameDto.getHighScore();
        this.operationCounter = gameDto.getOperationCounter();
        this.missionBatchCounter = gameDto.getMissionBatchCounter();
        this.missionExecutedCounter = gameDto.getMissionExecutedCounter();
        this.missionFailedCounter = gameDto.getMissionFailedCounter();
        this.notFoundBatchReloadCounter = gameDto.getNotFoundBatchReloadCounter();
        this.expiredMissionsCounter = gameDto.getExpiredMissionsCounter();
        this.oosMissionsCounter = gameDto.getOosMissionsCounter();
        this.notFoundMissionsCounter = gameDto.getNotFoundMissionsCounter();
        this.potentialScore = gameDto.getPotentialScore();
        this.startTime = gameDto.getStartTime();
        this.endTime = gameDto.getEndTime();
        this.outcome = gameDto.getOutcome();
    }

    public GameDto toDto() {
        return GameDto.builder()
                .id(this.id)
                .gameId(this.gameId)
                .lives(this.lives)
                .gold(this.gold)
                .level(this.level)
                .score(this.score)
                .turn(this.turn)
                .highScore(this.highScore)
                .operationCounter(this.operationCounter)
                .missionBatchCounter(this.missionBatchCounter)
                .missionExecutedCounter(this.missionExecutedCounter)
                .missionFailedCounter(this.missionFailedCounter)
                .notFoundBatchReloadCounter(this.notFoundBatchReloadCounter)
                .expiredMissionsCounter(this.expiredMissionsCounter)
                .oosMissionsCounter(this.oosMissionsCounter)
                .notFoundMissionsCounter(this.notFoundMissionsCounter)
                .potentialScore(this.potentialScore)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .outcome(this.outcome)
                .build();
    }

}
