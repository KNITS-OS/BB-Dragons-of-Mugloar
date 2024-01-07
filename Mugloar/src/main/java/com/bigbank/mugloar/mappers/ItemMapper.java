package com.bigbank.mugloar.mappers;

import com.bigbank.mugloar.dto.domain.core.ItemDto;
import com.bigbank.mugloar.model.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper extends EntityMapper<Item, ItemDto>{



    @Mapping(source="itemId", target = "externalId")
    Item toEntity(ItemDto dto);
    @Mapping(source="externalId", target = "itemId")
    ItemDto toDto(Item entity);



}
