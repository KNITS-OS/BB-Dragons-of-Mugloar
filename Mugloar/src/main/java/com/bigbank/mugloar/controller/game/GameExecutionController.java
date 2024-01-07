package com.bigbank.mugloar.controller.game;

import com.bigbank.mugloar.dto.api.game.command.StartGameExecutionRequest;
import com.bigbank.mugloar.dto.api.game.command.StartGameExecutionResponse;
import com.bigbank.mugloar.dto.api.game.queries.GameExecutionResponse;
import com.bigbank.mugloar.dto.domain.game.GameExecutionConfigDto;
import com.bigbank.mugloar.dto.domain.game.GameExecutionDto;
import com.bigbank.mugloar.dto.domain.game.GameExecutionReportDto;
import com.bigbank.mugloar.service.game.GameExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/game-executions")
@Slf4j
public class GameExecutionController {

    private final GameExecutionService gameExecutionService;
    @Operation(summary = "Start a new game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Start a new Game",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StartGameExecutionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data type or null value supplied",
                    content = {@Content})})
    @PostMapping("/start")
    public ResponseEntity<StartGameExecutionResponse> startGame(@RequestBody StartGameExecutionRequest request) {
        GameExecutionConfigDto gameConfig =request.toDto();
        log.debug("Game Start Request with game Config {} ",gameConfig.toString());
        GameExecutionReportDto report =gameExecutionService.runGame(gameConfig);
        return new ResponseEntity<>(new StartGameExecutionResponse(report), HttpStatus.OK);
    }

    @Operation(summary = "Get Game Execution Report with all Events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get game events",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StartGameExecutionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data type or null value supplied",
                    content = {@Content})})
    @GetMapping("/{gameId}")
    public ResponseEntity<GameExecutionResponse> findByExternalId(@Parameter(description = "Game external id") @PathVariable("gameId") String gameId) {
        log.debug("Find Game Execution Event Request for gameId {} ",gameId);
        GameExecutionDto gameExecution =gameExecutionService.findGameByExternalId(gameId);
        return new ResponseEntity<>(new GameExecutionResponse(gameExecution), HttpStatus.OK);
    }

    @Operation(summary = "Get Game Execution Report with all Event details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get game events",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = StartGameExecutionResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data type or null value supplied",
                    content = {@Content})})
    @GetMapping("/{gameId}/details")
    public ResponseEntity<GameExecutionResponse> findGameDetails(@Parameter(description = "Game external id") @PathVariable("gameId") String gameId) {
        log.debug("Find Game Details Request for gameId {} ",gameId);
        GameExecutionDto gameExecution =gameExecutionService.findGameByExternalId(gameId);
        return new ResponseEntity<>(new GameExecutionResponse(gameExecution), HttpStatus.OK);
    }

}
