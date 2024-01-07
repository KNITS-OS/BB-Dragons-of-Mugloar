package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.dto.domain.core.GameDto;

public interface GameStateService {

    GameDto createNewGame(GameDto newGame);
    GameDto getCurrentGameById(String gameId);
    GameDto updateCache(GameDto updatedGame);
     void finalizeGame(String gameId);
}
