package com.bigbank.mugloar.controller.game;

import com.bigbank.mugloar.dto.api.game.command.StartGameExecutionRequest;
import com.bigbank.mugloar.dto.api.game.command.StartGameExecutionResponse;
import com.bigbank.mugloar.dto.api.game.queries.GameExecutionResponse;
import com.bigbank.mugloar.dto.domain.game.GameExecutionDto;
import com.bigbank.mugloar.dto.domain.game.GameExecutionReportDto;
import com.bigbank.mugloar.dto.domain.game.GameExecutionConfigDto;
import com.bigbank.mugloar.mock.dto.game.GameExecutionDtoMock;
import com.bigbank.mugloar.service.game.GameExecutionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameExecutionControllerTest {

    @Mock
    private GameExecutionService gameExecutionService;

    @InjectMocks
    private GameExecutionController gameExecutionController;

    @Test
    void testStartGame() {
        // Given
        StartGameExecutionRequest mockRequest = new StartGameExecutionRequest();
        GameExecutionReportDto mockReport = new GameExecutionReportDto();
        GameExecutionConfigDto gameConfig= new GameExecutionConfigDto();
        when(gameExecutionService.runGame(gameConfig)).thenReturn(mockReport);

        // When
        ResponseEntity<StartGameExecutionResponse> responseEntity = gameExecutionController.startGame(mockRequest);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().toDto()).isEqualTo(mockReport);

        // Verify that the service method was called
        verify(gameExecutionService, times(1)).runGame(gameConfig);
    }

    @Test
    void testFindByExternalId() {
        // Given
        String gameId = "mockGameId";
        GameExecutionDto mockGameExecutionDto = GameExecutionDtoMock.shallowGameExecutionDto(gameId);
        when(gameExecutionService.findGameByExternalId(gameId)).thenReturn(mockGameExecutionDto);

        // When
        ResponseEntity<GameExecutionResponse> responseEntity = gameExecutionController.findByExternalId(gameId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().toDto()).isEqualTo(mockGameExecutionDto);

        // Verify that the service method was called
        verify(gameExecutionService, times(1)).findGameByExternalId(gameId);
    }

    @Test
    void testFindGameDetails() {
        // Given
        String gameId = "mockGameId";
        GameExecutionDto mockGameExecutionDto = GameExecutionDtoMock.shallowGameExecutionDto(gameId);
        when(gameExecutionService.findGameByExternalId(gameId)).thenReturn(mockGameExecutionDto);

        // When
        ResponseEntity<GameExecutionResponse> responseEntity = gameExecutionController.findGameDetails(gameId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().toDto()).isEqualTo(mockGameExecutionDto);

        // Verify that the service method was called
        verify(gameExecutionService, times(1)).findGameByExternalId(gameId);
    }
}

