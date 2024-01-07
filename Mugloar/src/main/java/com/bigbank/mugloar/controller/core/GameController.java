package com.bigbank.mugloar.controller.core;

import com.bigbank.mugloar.dto.api.core.queries.GameResponse;
import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.service.core.GameService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/core/games")
@Slf4j
public class GameController {

    private final GameService gameService;

    @Operation(summary = "Get game details by external ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieve game details",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = GameResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = {@Content})})
    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> findByExternalId(@Parameter(description = "Game external id") @PathVariable("gameId") String gameId) {
        log.debug("Find Game Request for gameId {} ", gameId);
        GameDto game = gameService.findGameByGameId(gameId);
        return new ResponseEntity<>(new GameResponse(game), HttpStatus.OK);
    }
}
