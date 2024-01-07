package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.dto.domain.core.*;
import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.mappers.EventMapper;
import com.bigbank.mugloar.mappers.EventMapperImpl;
import com.bigbank.mugloar.mock.dto.core.*;
import com.bigbank.mugloar.mock.dto.game.EventDtoMock;
import com.bigbank.mugloar.mock.model.game.EventMock;
import com.bigbank.mugloar.model.Event;
import com.bigbank.mugloar.model.EventType;
import com.bigbank.mugloar.model.GameStateType;
import com.bigbank.mugloar.repository.game.EventRepository;
import com.bigbank.mugloar.service.game.impl.EventServiceBasicImpl;
import com.bigbank.mugloar.util.EventConsts;
import com.bigbank.mugloar.util.MissionConsts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Spy
    private EventMapper eventMapper= new EventMapperImpl();

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceBasicImpl eventService;

    @Test
    void testFindEventsByGameId() {

        int eventsExpected=20;
        List<Event> mockEvents= EventMock.shallowEvents(eventsExpected);

        when(eventRepository.findAllByGameExternalId(any())).thenReturn(mockEvents);

        List<EventDto> eventsFound = eventService.findEventsByGameId("testGameId");

        // Verify interactions
        verify(eventRepository, times(1)).findAllByGameExternalId("testGameId");
        verify(eventMapper, times(1)).toDtos(anyList());

        // Assert the result
        assertThat(eventsFound).isNotEmpty();
        assertThat(eventsFound.size()).isEqualTo(eventsExpected);
    }

    @Test
    void testSaveStartGameEvent() {

        String gameId="AMockGameId";
        GameDto gameState = GameDto.builder().gameId(gameId).build();
        when(eventRepository.save(any())).thenReturn(EventMock.shallowEvent(1L,EventType.GAME_START, EventConsts.GAME_STARTED));

        EventDto eventsaved= eventService.saveStartGameEvent(gameState);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(eventsaved).isNotNull();
    }

    @Test
    void testSavePurchasedItemEvent() {

        GameDto gameState = GameDtoMock.shallowGameDto(1L,"aMockGameId");
        ItemDto item = ItemDtoMock.shallowItemDto(1L, "hotpot");
        PurchasedItemDto itemPurchased = PurchasedItemDtoMock.shallowPurchasedItemDto("hotpot", EventConsts.ITEM_BUY_SUCCESS);
        when(eventRepository.save(any())).thenReturn(EventMock.shallowEvent(1L,EventType.PURCHASE_ITEM, EventConsts.ITEM_BUY_SUCCESS));

        EventDto result = eventService.savePurchasedItemEvent(gameState, item, itemPurchased);

        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        assertThat(result).isNotNull();
    }

    @Test
    void testSaveEndGameEvent() {

        GameDto endGameState = GameDtoMock.shallowGameDto(1L,"aMockGameId");
        endGameState.setOutcome(GameStateType.VICTORY);
        EventDto event = EventDtoMock.shallowEventDto(1L, EventType.GAME_OVER, "success");
        event.setOutcome(EventConsts.GAME_OVER);
        when(eventRepository.save(any())).thenReturn(EventMock.shallowEvent(1L,EventType.GAME_OVER, EventConsts.GAME_OVER));

        EventDto result = eventService.saveEndGameEvent(endGameState);

        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        assertThat(result).isNotNull();
    }

    @Test
    void testSaveExecutedMissionEvent() {
        missionEventsTestTemplate(EventType.EXECUTE_MISSION,EventConsts.MISSION_SUCCESS);
    }

    @Test
    void testSaveMissionOutOfSynchEvent() {
        missionEventsTestTemplate(EventType.SKIPPED_MISSION, EventConsts.MISSION_OUT_OF_SYNCH);
    }

    @Test
    void testSaveMissionNotFoundOnServerEvent() {
        missionEventsTestTemplate(EventType.SKIPPED_MISSION,EventConsts.MISSION_NOT_FOUND);
    }

    @Test
    void testSaveExpiredMissionEvent() {
        missionEventsTestTemplate(EventType.SKIPPED_MISSION,EventConsts.MISSION_EXPIRED);
    }

    @Test
    void testSaveDuplicatedMissionEvent() {
        missionEventsTestTemplate(EventType.SKIPPED_MISSION,EventConsts.MISSION_DUPLICATED);
    }

    private void missionEventsTestTemplate(EventType eventType, String outcome) {
        String gameId="AMockGameId";
        String missionAdid="AMockMissionadId";

        GameDto gameState = GameDto.builder().gameId(gameId).build();
        MissionDto mission = MissionDtoMock.shallowMissionDto(missionAdid,gameId);
        MissionResultDto missionResult = MissionResultDtoMock.shallowMissionResultDto(true);
        when(eventRepository.save(any())).thenReturn(EventMock.shallowEvent(1L,eventType, outcome));

        EventDto result = eventService.saveExecutedMissionEvent(gameState, mission, missionResult);
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo(eventType);
        assertThat(result.getOutcome()).isEqualTo(outcome);
        assertThat(result.getGameExternalId()).isEqualTo(gameId);
        assertThat(result.getEventExternalId()).isEqualTo(missionAdid);
    }
}
