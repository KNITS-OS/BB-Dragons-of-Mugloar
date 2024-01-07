package com.bigbank.mugloar.dto.api.game.command;

import com.bigbank.mugloar.dto.domain.game.GameExecutionConfigDto;
import com.bigbank.mugloar.util.Consts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StartGameExecutionRequest {

    @Builder.Default
    private int numberOfGames=1;

    @Builder.Default
    private boolean async=true;

    @Builder.Default
    private String callbackUrl= Consts.CALLBACK_URL_NONE;


    public StartGameExecutionRequest(GameExecutionConfigDto config){
        this.numberOfGames = config.getNumberOfGames();
        this.async = config.isAsync();
        this.callbackUrl = config.getCallbackUrl();

    }
    public GameExecutionConfigDto toDto(){
        return GameExecutionConfigDto.builder()
                .async(async)
                .callbackUrl(callbackUrl)
                .numberOfGames(numberOfGames)
                .build();
    }
}
