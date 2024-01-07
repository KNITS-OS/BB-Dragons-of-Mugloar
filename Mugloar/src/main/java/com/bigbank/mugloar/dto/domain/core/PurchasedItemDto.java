package com.bigbank.mugloar.dto.domain.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasedItemDto {

    private Long id;
    private String itemId;
    private String shoppingSuccess;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private int level;
    private LocalDateTime time;

    private GameDto game;
    private ItemDto itemDto;
}
