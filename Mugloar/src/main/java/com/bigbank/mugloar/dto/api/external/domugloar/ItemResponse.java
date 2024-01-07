package com.bigbank.mugloar.dto.api.external.domugloar;

import com.bigbank.mugloar.dto.domain.core.ItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemResponse {

    private String id;
    private String name;
    private double cost;

    public ItemDto toDto(){
        return ItemDto.builder()
                .itemId(id)
                .cost(cost)
                .name(name)
                .build();
    }
}
