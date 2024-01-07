package com.bigbank.mugloar.dto.domain.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {

    private Long id;
    private String itemId;
    private String name;
    private double cost;


}
