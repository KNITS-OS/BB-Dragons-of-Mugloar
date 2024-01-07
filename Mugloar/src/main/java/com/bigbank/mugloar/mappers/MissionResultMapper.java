package com.bigbank.mugloar.mappers;

import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.model.MissionResult;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = MissionMapper.class)
public interface MissionResultMapper extends EntityMapper<MissionResult, MissionResultDto>{
}
