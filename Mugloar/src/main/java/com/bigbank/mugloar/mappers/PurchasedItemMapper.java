package com.bigbank.mugloar.mappers;

import com.bigbank.mugloar.dto.domain.core.PurchasedItemDto;
import com.bigbank.mugloar.model.PurchasedItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ItemMapper.class)
public interface PurchasedItemMapper extends EntityMapper<PurchasedItem, PurchasedItemDto>{

    @Mapping(source="item.externalId", target = "itemId")
    PurchasedItemDto toDto(PurchasedItem entity);

}
