package com.bigbank.mugloar.mock.dto.core;

import com.bigbank.mugloar.dto.domain.core.PurchasedItemDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class PurchasedItemDtoMock {

    public static PurchasedItemDto shallowPurchasedItemDto(String itemId, String shoppingSuccess) {
        return PurchasedItemDto.builder()
                .itemId(itemId)
                .shoppingSuccess(shoppingSuccess)
                .lives(2)
                .gold(50)
                .score(1500)
                .highScore(2000)
                .turn(8)
                .level(3)
                .time(LocalDateTime.now())
                .game(GameDtoMock.shallowGameDto(1L, "MockGame"))
                .itemDto(ItemDtoMock.shallowItemDto(1L, "MockItem"))
                .build();
    }
}
