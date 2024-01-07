package com.bigbank.mugloar.mappers;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.model.Mission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MissionMapper extends EntityMapper<Mission, MissionDto>{

    @Mapping(source="adId", target = "externalId")
    Mission toEntity(MissionDto dto);

    @Mapping(source="externalId", target = "adId")
    MissionDto toDto(Mission entity);
}
