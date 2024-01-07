package com.bigbank.mugloar.exception;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MissionExpiredException extends MissionSkipException{

    public MissionExpiredException(String gameId, MissionDto mission, String message ) {
        super(gameId, mission, message);
    }
}
