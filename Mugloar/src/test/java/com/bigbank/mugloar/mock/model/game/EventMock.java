package com.bigbank.mugloar.mock.model.game;

import com.bigbank.mugloar.model.Event;
import com.bigbank.mugloar.model.EventType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
public class EventMock {

    public static Event shallowEvent(Long id, EventType eventType, String outcome) {
        return Event.builder()
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
                .operationCounter(42)
                .operationAmount(500)
                .type(eventType)
                .outcome(outcome)
                .build();
    }

    public static List<Event> shallowEvents(int howMany) {
        List<Event> events = new ArrayList<>();
        for (long id = 1; id <= howMany; id++) {
            events.add(shallowEvent(id, EventType.EXECUTE_MISSION, "success"));
        }
        return events;
    }
}
