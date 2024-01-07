package com.bigbank.mugloar.controller.core;


import com.bigbank.mugloar.dto.api.core.queries.GameResponse;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.mock.core.GameDtoMock;
import com.bigbank.mugloar.service.core.GameService;
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
public class GameControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @Test
    void testFindByExternalId() {
        // Given
        String gameId = "mockGameId";
        GameDto mockGameDto = GameDtoMock.shallowGameDto(1L, gameId);
        when(gameService.findGameByGameId(gameId)).thenReturn(mockGameDto);

        // When
        ResponseEntity<GameResponse> responseEntity = gameController.findByExternalId(gameId);

        // Then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().toDto().getGameId()).isEqualTo(gameId);

        // Verify that the service method was called
        verify(gameService, times(1)).findGameByGameId(gameId);
    }
}

