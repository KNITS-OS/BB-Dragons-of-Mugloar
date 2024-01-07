package com.bigbank.mugloar.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gameExternalId;
    private String eventExternalId;

    @Column(columnDefinition = "integer default 0")
    private int missionBatchCounter;

    private int gameLives;
    private int responseLives;
    private int gameGold;
    private int responseGold;
    private int gameLevel;
    private int responseLevel;
    private int gameScore;
    private int responseScore;
    private int gameTurn;
    private int responseTurn;

    @Column(columnDefinition = "integer default 0")
    private int operationCounter;

    @Column(columnDefinition = "integer default 0")
    private int operationAmount;

    private String outcome;

    @Enumerated(EnumType.STRING)
    private EventType type;
}
