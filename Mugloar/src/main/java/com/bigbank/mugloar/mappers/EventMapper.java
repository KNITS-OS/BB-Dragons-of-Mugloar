package com.bigbank.mugloar.mappers;

import com.bigbank.mugloar.dto.domain.game.EventDto;
import com.bigbank.mugloar.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper extends EntityMapper<Event, EventDto>{


}
