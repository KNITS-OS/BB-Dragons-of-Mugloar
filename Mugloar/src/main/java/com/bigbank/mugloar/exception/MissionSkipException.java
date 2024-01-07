package com.bigbank.mugloar.exception;

import com.bigbank.mugloar.dto.domain.core.MissionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class MissionSkipException extends RuntimeException{

    private String gameId;
    private MissionDto mission;
    private String message;

}
