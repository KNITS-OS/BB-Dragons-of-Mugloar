package com.bigbank.mugloar.exception;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import com.bigbank.mugloar.dto.domain.core.MissionResultDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MissionOutOfSyncException extends MissionSkipException {

    private MissionResultDto missionResult;
    public MissionOutOfSyncException(String gameId,MissionDto mission,MissionResultDto missionResult,String message) {
        super(gameId, mission, message);
        this.missionResult = missionResult;
    }
}
