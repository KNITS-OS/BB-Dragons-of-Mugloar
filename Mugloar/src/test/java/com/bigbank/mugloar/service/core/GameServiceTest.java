package com.bigbank.mugloar.service.core;

import com.bigbank.mugloar.dto.api.external.domugloar.StartGameResponse;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.mappers.GameMapper;
import com.bigbank.mugloar.mappers.GameMapperImpl;
import com.bigbank.mugloar.model.Game;
import com.bigbank.mugloar.proxy.GameProxy;
import com.bigbank.mugloar.repository.core.GameRepository;
import com.bigbank.mugloar.service.core.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class GameServiceTest {


    @Mock
    private GameProxy gameProxy;

    @Spy
    private GameMapper gameMapper = new GameMapperImpl();

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;
    @Test
    public void testPersistGame() {
        // Given
        GameDto inputGameDto = new GameDto("AMockId");
        Game inputGameEntity = gameMapper.toEntity(inputGameDto);
        inputGameEntity.setId(1L);

        when(gameRepository.save(any())).thenReturn(inputGameEntity);

        // When
        GameDto result = gameService.persistGame(inputGameDto);
        result.setId(1L);
        inputGameDto.setId(1L);

        // Then
        verify(gameMapper, times(2)).toEntity(inputGameDto);
        verify(gameRepository, times(1)).save(any());
        verify(gameMapper, times(1)).toDto(inputGameEntity);
        assertThat(result).isEqualToComparingFieldByField(inputGameDto);
    }

    @Test
    public void testUpdateGame() {
        // Given
        Long savedGameId=1L;
        String gameId = "123";
        GameDto updatedGameDto = GameDto.builder().id(savedGameId).gameId(gameId).lives(3).score(120).build();

        Game existingGame = Game.builder().lives(2).score(100).build();
        when(gameRepository.findOneByGameId(gameId)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> {
            Game savedGame = (Game)invocation.getArgument(0);
            savedGame.setId(savedGameId);
            return savedGame;
        });

        // When
        GameDto result = gameService.updateGame(updatedGameDto);

        // Then
        verify(gameRepository, times(1)).findOneByGameId(gameId);
        verify(gameMapper, times(1)).partialUpdate(any(), any());
        verify(gameRepository, times(1)).save(any());
        verify(gameMapper, times(1)).toDto(any());
        assertThat(result).isEqualToComparingFieldByField(updatedGameDto);

    }

    @Test
    public void testStartGame() {
        // Given
        String gameId = "123";
        StartGameResponse startGameResponse = new StartGameResponse();
        startGameResponse.setGameId(gameId);
        when(gameProxy.startGame()).thenReturn(startGameResponse);

        // When
        String result = gameService.startGame();

        // Then
        assertThat(result).isEqualTo(gameId);
        verify(gameProxy, times(1)).startGame();
    }

    @Test
    public void testFindGameByGameId() {
        // Given
        String gameId = "123";
        Game existingGame = Game.builder().gameId(gameId).lives(2).score(100).build();
        GameDto expectedDto =gameMapper.toDto(existingGame);
        when(gameRepository.findOneByGameId(gameId)).thenReturn(Optional.of(existingGame));

        // When
        GameDto result = gameService.findGameByGameId(gameId);

        // Then
        verify(gameRepository, times(1)).findOneByGameId(gameId);
        verify(gameMapper, times(2)).toDto(any(Game.class));
        assertThat(result).isEqualToComparingFieldByField(expectedDto);
    }
}
