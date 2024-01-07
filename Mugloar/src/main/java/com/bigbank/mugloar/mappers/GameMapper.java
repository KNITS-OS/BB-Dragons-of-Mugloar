package com.bigbank.mugloar.mappers;

import com.bigbank.mugloar.dto.domain.core.GameDto;
import com.bigbank.mugloar.model.Game;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GameMapper extends EntityMapper<Game, GameDto>{

}
