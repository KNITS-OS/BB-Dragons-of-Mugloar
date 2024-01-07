package com.bigbank.mugloar.exception;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MissionDuplicatedException extends MissionSkipException{

    public MissionDuplicatedException(String gameId, MissionDto mission, String message ) {
        super(gameId, mission, message);
    }
}
