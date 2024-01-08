package com.bigbank.mugloar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int highScore;
    private int turn;

    @Column(columnDefinition = "integer default 0")
    private int operationCounter;

    @Column(columnDefinition = "integer default 0")
    private int missionBatchCounter;

    @Column(columnDefinition = "integer default 0")
    private int missionExecutedCounter;

    @Column(columnDefinition = "integer default 0")
    private int missionFailedCounter;

    @Column(columnDefinition = "integer default 0")
    private int notFoundBatchReloadCounter;

    @Column(columnDefinition = "integer default 0")
    private int expiredMissionsCounter;

    @Column(columnDefinition = "integer default 0")
    private int oosMissionsCounter;

    @Column(columnDefinition = "integer default 0")
    private int notFoundMissionsCounter;

    @Column(columnDefinition = "integer default 0")
    private int potentialScore;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private GameStateType outcome;


    @OneToMany(mappedBy = "game")
    private List<Mission> missions;
}
