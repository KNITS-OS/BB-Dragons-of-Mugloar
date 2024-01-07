package com.bigbank.mugloar.exception;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import com.bigbank.mugloar.model.Mission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MissionNotFoundException extends MissionSkipException{

    public MissionNotFoundException(String gameId, MissionDto mission, String message ) {
        super(gameId, mission, message);
    }

}
