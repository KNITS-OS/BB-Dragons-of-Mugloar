package com.bigbank.mugloar.dto.domain.game;

import com.bigbank.mugloar.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {

    private Long id;
    private String gameExternalId;
    private String eventExternalId;
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
    private int highScore;
    private int operationCounter;
    private int operationAmount;
    private EventType type;
    private String outcome;
}
