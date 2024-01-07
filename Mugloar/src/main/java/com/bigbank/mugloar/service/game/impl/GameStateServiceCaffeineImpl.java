package com.bigbank.mugloar.service.game.impl;

import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.service.core.GameService;
import com.bigbank.mugloar.service.game.GameStateService;
import com.bigbank.mugloar.util.Consts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static java.lang.String.format;

@RequiredArgsConstructor
@Service
@Slf4j
public class GameStateServiceCaffeineImpl implements GameStateService {


    private final GameService gameService;

    @CachePut(value = Consts.GAME_CACHE, key = "#newGame.gameId")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GameDto createNewGame(GameDto newGame){
        GameDto savedGame =gameService.persistGame(newGame);
        savedGame.setExecutedMissionsIds(new HashSet<>()); //this is not saved, and need to be initialized in cache
        return savedGame;
    }
    @Cacheable(value = Consts.GAME_CACHE, key = "#gameId")
    public GameDto getCurrentGameById(String gameId){
        throw new IllegalStateException(format("No Game found in cache for Game: %s ",gameId));
    }

    @CachePut(value = Consts.GAME_CACHE, key = "#updatedGame.gameId")
    public GameDto updateCache(GameDto updatedGame){
      log.info("{} Cache updated on key {} with Game data: {} ",Consts.GAME_CACHE,updatedGame.getGameId(), updatedGame.toString());
      return updatedGame;
    }

    @CacheEvict(value = Consts.GAME_CACHE, key = "#gameId", beforeInvocation = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finalizeGame(String gameId) {
        log.info("Evicted Game: {} from cache..",gameId);
    }

}
