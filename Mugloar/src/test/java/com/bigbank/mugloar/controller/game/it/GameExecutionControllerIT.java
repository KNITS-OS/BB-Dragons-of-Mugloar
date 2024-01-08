package com.bigbank.mugloar.controller.game.it;

import com.bigbank.mugloar.dto.api.game.command.StartGameExecutionRequest;
import com.bigbank.mugloar.dto.api.game.command.StartGameExecutionResponse;
import com.bigbank.mugloar.dto.api.game.queries.GameExecutionResponse;
import com.bigbank.mugloar.dto.domain.game.GameExecutionConfigDto;
import com.bigbank.mugloar.service.game.GameExecutionService;
import com.bigbank.mugloar.util.Consts;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@TestPropertySource(locations = "classpath:application-integration-test.yaml")
class GameExecutionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameExecutionService gameExecutionService;

    @Autowired
    private ObjectMapper om;

    @Test
    @DisplayName("it should start game asynchronously by default")
    void startGameAsynchronouslyByDefault() throws Exception {

        int gamesToRun = 5;
        GameExecutionConfigDto config = GameExecutionConfigDto.builder().
                numberOfGames(gamesToRun).
                build();

        ResultActions result = startGame(config);
        StartGameExecutionResponse startGameResponse = assertionsAsynchGame(result, gamesToRun);

        String gameId = startGameResponse.getGameIds().stream().collect(Collectors.toList()).get(0);
        Awaitility.await()
                .atMost(30, SECONDS)
                .pollInterval(1, SECONDS)
                .until(() -> {
                    performQueryByGameIdCall(gameId);
                    return true;
                });
    }

    @Test
   // @Disabled("Extremely slow execution. Activate on demand by the code")
    @DisplayName("it should wait until response is returned ")
    void startGameSynchronously() throws Exception {
        int gamesToRun = 1;
        GameExecutionConfigDto config = GameExecutionConfigDto.builder().
                async(false).
                numberOfGames(gamesToRun).
                build();

        ResultActions result = startGame(config);
        assertionsSynchGame(result, gamesToRun);

    }


    private ResultActions startGame(GameExecutionConfigDto config) throws Exception {
        StartGameExecutionRequest startGameRequest = new StartGameExecutionRequest(config);
        String jsonRequest = om.writeValueAsString(startGameRequest);

        ResultActions result = mockMvc.perform(post("/api/game-executions/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.gameIds").isNotEmpty())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.code").isNumber());
        return result;
    }

    private void performQueryByGameIdCall(String gameId) throws Exception {
        ResultActions queryResult = mockMvc.perform(get("/api/game-executions/" + gameId)
                .contentType(MediaType.APPLICATION_JSON));
        queryResult.andExpect(status().isOk());
        String queryResultAsString = queryResult.andReturn().getResponse().getContentAsString();
        GameExecutionResponse gameExecutionResponse = om.readValue(queryResultAsString, GameExecutionResponse.class);
        assertThat(gameExecutionResponse.getGameId()).isEqualTo(gameId);
        assertThat(gameExecutionResponse.getEvents()).isNotEmpty();
    }

    private StartGameExecutionResponse assertionsAsynchGame(ResultActions result, int expectedGamesToRun) throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        StartGameExecutionResponse startGameResponse = om.readValue(contentAsString, StartGameExecutionResponse.class);
        assertThat(startGameResponse.getGameIds()).isNotEmpty();
        assertThat(startGameResponse.getGameIds().size()).isEqualTo(expectedGamesToRun);
        assertThat(startGameResponse.getCode()).isEqualTo(Consts.GAME_START_SUCCESS);
        return startGameResponse;
    }

    private StartGameExecutionResponse assertionsSynchGame(ResultActions result, int expectedGamesToRun) throws Exception {
        String contentAsString = result.andReturn().getResponse().getContentAsString();
        StartGameExecutionResponse startGameResponse = om.readValue(contentAsString, StartGameExecutionResponse.class);
        assertThat(startGameResponse.getGameIds()).isNotEmpty();
        assertThat(startGameResponse.getGameIds().size()).isEqualTo(expectedGamesToRun);
        assertThat(startGameResponse.getCode()).isEqualTo(Consts.GAME_COMPLETED);
        return startGameResponse;
    }

}
