package com.bigbank.mugloar.dto.domain.game;

import com.bigbank.mugloar.util.Consts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameExecutionConfigDto {

    @Builder.Default
    private int numberOfGames=1;

    @Builder.Default
    private boolean async=true;

    @Builder.Default
    private String callbackUrl= Consts.CALLBACK_URL_NONE;



}
