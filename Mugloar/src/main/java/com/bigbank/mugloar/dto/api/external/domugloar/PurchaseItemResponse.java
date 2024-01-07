package com.bigbank.mugloar.dto.api.external.domugloar;

import com.bigbank.mugloar.dto.domain.core.PurchasedItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseItemResponse {

    private String shoppingSuccess;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private int level;


    public PurchasedItemDto toDto(){
        return PurchasedItemDto.builder()
                .level(this.level)
                .lives(this.lives)
                .score(this.score)
                .gold(this.gold)
                .turn(this.turn)
                .highScore(this.highScore)
                .shoppingSuccess(shoppingSuccess)
                .build();
    }

}
