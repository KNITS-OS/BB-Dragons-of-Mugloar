package com.bigbank.mugloar.mock.game;

import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.model.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@Builder
public class EventDtoMock {

    public static EventDto shallowEventDto(Long id,EventType eventType, String outcome) {
        return EventDto.builder()
                .id(id)
                .gameExternalId("Mock-GameExternalId")
                .eventExternalId("Mock-EventExternalId")
                .missionBatchCounter(1)
                .gameLives(3)
                .responseLives(2)
                .gameGold(100)
                .responseGold(50)
                .gameLevel(5)
                .responseLevel(4)
                .gameScore(2000)
                .responseScore(1800)
                .gameTurn(10)
                .responseTurn(9)
                .highScore(2500)
                .operationCounter(42)
                .operationAmount(500)
                .type(eventType)
                .outcome(outcome)
                .build();
    }
}
