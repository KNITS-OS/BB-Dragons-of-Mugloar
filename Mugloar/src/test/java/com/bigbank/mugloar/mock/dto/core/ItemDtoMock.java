package com.bigbank.mugloar.mock.dto.core;

import com.bigbank.mugloar.dto.domain.core.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
public class ItemDtoMock {

    public static ItemDto shallowItemDto(Long id, String itemId) {
        return ItemDto.builder()
                .id(id)
                .itemId(itemId)
                .name(itemId)
                .cost(100.0)
                .build();
    }

    public static Map<String, ItemDto> shallowItemDtos(int howMany) {
        Map<String, ItemDto> itemDtos = new HashMap<>();

        for (long id = 1; id <= howMany; id++) {
            String itemId = "Item" + id;
            ItemDto itemDto = shallowItemDto(id, itemId);
            itemDtos.put(itemId, itemDto);
        }

        return itemDtos;
    }
}
