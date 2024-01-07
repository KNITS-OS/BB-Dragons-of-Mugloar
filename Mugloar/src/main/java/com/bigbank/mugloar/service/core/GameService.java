package com.bigbank.mugloar.service.core;

import com.bigbank.mugloar.dto.api.external.domugloar.StartGameResponse;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.mappers.GameMapper;
import com.bigbank.mugloar.model.Game;
import com.bigbank.mugloar.proxy.GameProxy;
import com.bigbank.mugloar.repository.core.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class GameService {

    private final GameProxy gameProxy;
    private final GameMapper gameMapper;
    private final GameRepository gameRepository;

    public GameDto persistGame(GameDto gameExecutionSummary) {
        Game entity = gameMapper.toEntity(gameExecutionSummary);
        Game persisted = gameRepository.save(entity);
        return gameMapper.toDto(persisted);
    }

    public GameDto updateGame(GameDto updatedGame) {
        Game existingGame = gameRepository.findOneByGameId(updatedGame.getGameId())
                .orElseThrow(() -> new IllegalStateException(format("Game for GameId: %s Not found", updatedGame.getGameId())));
        gameMapper.partialUpdate(existingGame, updatedGame);
        Game persisted = gameRepository.save(existingGame);
        return gameMapper.toDto(persisted);
    }

    public String startGame() {
        StartGameResponse gameResponse = gameProxy.startGame();
        return gameResponse.getGameId();
    }

    public GameDto findGameByGameId(String gameId) {
        return gameMapper.toDto(gameRepository.findOneByGameId(gameId).orElseThrow(
                () -> new IllegalStateException(format("Game for GameId: %s Not found", gameId))));
    }
}
