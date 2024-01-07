package com.bigbank.mugloar.service.game;

import com.bigbank.mugloar.config.ApplicationProperties;
import com.bigbank.mugloar.dto.api.external.domugloar.ItemResponse;
import com.bigbank.mugloar.dto.domain.core.*;
import com.bigbank.mugloar.dto.domain.game.*;
import com.bigbank.mugloar.exception.GameOverException;
import com.bigbank.mugloar.exception.MissionBatchException;
import com.bigbank.mugloar.processor.MissionProcessor;
import com.bigbank.mugloar.service.core.*;
import com.bigbank.mugloar.service.game.impl.GameStateServiceCaffeineImpl;
import com.bigbank.mugloar.util.Consts;
import com.bigbank.mugloar.util.ItemConsts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CompletableFuture;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class GameExecutionService {

    private final ApplicationProperties appConfig;
    private final ExecutorService executor;

    private final GameService gameService;
    private final MissionService missionService;

    private final EventService eventService;

    private final MissionProcessor missionProcessor;
    private final GameStateServiceCaffeineImpl gameStateService;
    private final ItemStateService itemStateService;
    private final ItemService itemService;


    public GameExecutionReportDto runGame(GameExecutionConfigDto gameConfig) {

        log.info("Start {} Games in {} threads ", gameConfig.getNumberOfGames(), appConfig.getAsync().getAsyncExecutorPoolSize());
        long startTime = System.currentTimeMillis();

        Set<GameDto> gamesStarted = startGames(gameConfig);

        if(gameConfig.isAsync()){
            runGamesAsync(startTime,gameConfig,gamesStarted);
            return GameExecutionReportDto.builder()
                    .gameIds(gamesStarted.stream().map(game->game.getGameId()).collect(Collectors.toSet()))
                    .code(Consts.GAME_START_SUCCESS)
                    .message("Game started successfully. Query api for current results, or wait for feedback at callback configured endpoint")
                    .build();
        }

        Set<String> completedGameIds = executeInCurrentThread(startTime,gameConfig,gamesStarted);
        return GameExecutionReportDto.builder()
                .gameIds(completedGameIds)
                .code(Consts.GAME_COMPLETED)
                .message("All games completed. Game reports are available in api")
                .build();


    }

    @Scheduled(initialDelay = 3000)
    public void autoStartGame(){
        if(!appConfig.isAutoStartGame()){
            log.info("Auto start disabled. Game will run only on demand");
            return;
        }

        log.info("Auto start enabled. Starting a new Game...");
        GameExecutionConfigDto gameConfig= new GameExecutionConfigDto();
        gameConfig.setNumberOfGames(1);

        log.info("Start {} Games in {} threads ", gameConfig.getNumberOfGames(), appConfig.getAsync().getAsyncExecutorPoolSize());
        long startTime = System.currentTimeMillis();
        GameDto currentGame=initializeGame();
        executeInCurrentThread(startTime,gameConfig,Set.of(currentGame));

    }

    private Set<GameDto> startGames(GameExecutionConfigDto gameConfig){
        Set<GameDto> games = new HashSet<>();
        for (int i = 0; i < gameConfig.getNumberOfGames(); i++) {
            games.add(initializeGame());
        }
        return games;
    }

    private void runGamesAsync(long startTime,GameExecutionConfigDto gameConfig, Set<GameDto> gamesStarted){
        List<CompletableFuture<GameDto>> futures = new ArrayList<>();
        try {
            for (GameDto startedGame : gamesStarted) {
                CompletableFuture<GameDto> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return runGameInternal(startedGame);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        return gameOnFailedExecution(startedGame);
                    }
                }, executor);
                future.thenAccept(completedGame -> sendFeedback(gameConfig,completedGame));
                futures.add(future);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Set<String> executeInCurrentThread(long startTime, GameExecutionConfigDto gameConfig, Set<GameDto> gamesStarted) {
        List<GameDto> completedGames=new ArrayList<>();
        for (GameDto startedGame : gamesStarted) {
            completedGames.add(runGameInternal(startedGame));
        }
        logExecutionTime(startTime);
        return completedGames.stream().map(game->game.getGameId()).collect(Collectors.toSet());
    }

    private void sendFeedback(GameExecutionConfigDto gameConfig, GameDto completedGame) {
        log.info("To be implemented. Send finalized game data {}  to {} url provided ",completedGame.toString(),gameConfig.getCallbackUrl());
    }


    private GameDto gameOnFailedExecution(GameDto currentGame){
        GameDto latestCachedState= gameStateService.getCurrentGameById(currentGame.getGameId());
        if(latestCachedState==null){
            log.error("No Game found in cache. Original state will be returned instead {} ",currentGame.toString());
            return currentGame;
        }
        return latestCachedState;
    }

    private GameExecutionReportDto createGameExecutionReport( List<GameDto> completedGames){
        return null;
    }

        private GameDto runGameInternal(GameDto currentGame) {

        while (currentGame.isRunning()) {
            try{
                MissionBatchDto nextMissionBatch =missionService.loadNewMissionBatch(currentGame.getGameId());
                missionProcessor.processCurrentMissionBatch(nextMissionBatch);

            }catch (MissionBatchException e){
                log.info("{} a new mission batch will be loaded", e.getMessage());
            }catch (GameOverException e){
                log.info("Game Over Because of {} ",e.getMessage());
                break;
            }
        }
        finalizeGame(currentGame.getGameId());
        return currentGame;
    }

    public GameExecutionDto findGameByExternalId(String gameId) {
        List<EventDto> events =eventService.findEventsByGameId(gameId);
        GameDto game = gameService.findGameByGameId(gameId);
        return GameExecutionDto.builder().gameId(gameId).events(events).game(game).build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private GameDto initializeGame (){
        String gameId=gameService.startGame();
        GameDto currentGame = gameStateService.createNewGame(new GameDto(gameId));
        eventService.saveStartGameEvent(currentGame);
        loadItemsIntoCurrentGame(gameId);
        return currentGame;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void finalizeGame(String gameId){
        GameDto currentGame =gameStateService.getCurrentGameById(gameId);
        currentGame.gameOver();
        eventService.saveEndGameEvent(currentGame);
        GameDto finalizedGame =gameService.updateGame(currentGame);
        log.info("Game: {} final state saved on DB",finalizedGame.toString());
        gameStateService.finalizeGame(gameId);
    }

    private void logExecutionTime(long startTime){
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        log.info("Game Execution completed in {} millis ", executionTime);
    }

    public void loadItemsIntoCurrentGame(String gameId){
        Map<String, ItemDto> availableItems = itemService.listItems(gameId).stream().collect(Collectors.toMap(ItemResponse::getId, itemDto -> itemDto.toDto()));
        itemStateService.initAvailableItems(gameId,availableItems);
        itemStateService.initPrioritizedItemKeys(gameId,new LinkedList<String>(ItemConsts.POWER_ITEMS_100));
    }
}
