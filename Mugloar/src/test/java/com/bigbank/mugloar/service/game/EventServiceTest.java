package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.dto.domain.core.*;
import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.mappers.EventMapper;
import com.bigbank.mugloar.mappers.EventMapperImpl;
import com.bigbank.mugloar.mock.game.EventDtoMock;
import com.bigbank.mugloar.model.EventType;
import com.bigbank.mugloar.model.GameStateType;
import com.bigbank.mugloar.repository.game.EventRepository;
import com.bigbank.mugloar.service.game.impl.EventServiceBasicImpl;
import com.bigbank.mugloar.util.EventConsts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceBasicImpl eventService;

    @Test
    void testFindEventsByGameId() {
        // Mock the repository response
        when(eventRepository.findAllByGameExternalId(any())).thenReturn(Collections.emptyList());

        // Perform the service method
        var result = eventService.findEventsByGameId("testGameId");

        // Verify interactions
        verify(eventRepository, times(1)).findAllByGameExternalId("testGameId");
        verify(eventMapper, times(1)).toDtos(anyList());

        // Assert the result
        assertThat(result).isEmpty();
    }

    @Test
    void testSaveStartGameEvent() {
        // Mock dependencies
        GameDto gameState = new GameDto();  // Provide a suitable GameDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.saveStartGameEvent(gameState);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSaveExecutedMissionEvent() {
        // Mock dependencies
        GameDto gameState = new GameDto();  // Provide a suitable GameDto for testing
        MissionDto mission = new MissionDto();  // Provide a suitable MissionDto for testing
        MissionResultDto missionResult = new MissionResultDto();  // Provide a suitable MissionResultDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.saveExecutedMissionEvent(gameState, mission, missionResult);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSavePurchasedItemEvent() {
        // Mock dependencies
        GameDto gameState = new GameDto();  // Provide a suitable GameDto for testing
        ItemDto item = new ItemDto();  // Provide a suitable ItemDto for testing
        PurchasedItemDto itemPurchased = new PurchasedItemDto();  // Provide a suitable PurchasedItemDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.savePurchasedItemEvent(gameState, item, itemPurchased);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSaveEndGameEvent() {
        // Mock dependencies
        GameDto endGameState = new GameDto();  // Provide a suitable GameDto for testing
        endGameState.setOutcome(GameStateType.VICTORY);
        EventDto event = EventDtoMock.shallowEventDto(1L, EventType.GAME_OVER, "success");
        event.setOutcome(EventConsts.GAME_OVER);
        when(eventMapper.toDto(any())).thenReturn(event);

        // Perform the service method
        var result = eventService.saveEndGameEvent(endGameState);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSaveMissionOutOfSynchEvent() {
        // Mock dependencies
        GameDto currentState = new GameDto();  // Provide a suitable GameDto for testing
        MissionDto mission = new MissionDto();  // Provide a suitable MissionDto for testing
        MissionResultDto missionResult = new MissionResultDto();  // Provide a suitable MissionResultDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.saveMissionOutOfSynchEvent(currentState, mission, missionResult);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSaveMissionNotFoundOnServerEvent() {
        // Mock dependencies
        GameDto currentState = new GameDto();  // Provide a suitable GameDto for testing
        MissionDto mission = new MissionDto();  // Provide a suitable MissionDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.saveMissionNotFoundOnServerEvent(currentState, mission);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSaveExpiredMissionEvent() {
        // Mock dependencies
        GameDto currentState = new GameDto();  // Provide a suitable GameDto for testing
        MissionDto mission = new MissionDto();  // Provide a suitable MissionDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.saveExpiredMissionEvent(currentState, mission);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }

    @Test
    void testSaveDuplicatedMissionEvent() {
        // Mock dependencies
        GameDto currentGame = new GameDto();  // Provide a suitable GameDto for testing
        MissionDto mission = new MissionDto();  // Provide a suitable MissionDto for testing
        when(eventMapper.toDto(any())).thenReturn(new EventDto());

        // Perform the service method
        var result = eventService.saveDuplicatedMissionEvent(currentGame, mission);

        // Verify interactions
        verify(eventMapper, times(1)).toDto(any());
        verify(eventRepository, times(1)).save(any());

        // Assert the result
        assertThat(result).isNotNull();
    }
}
