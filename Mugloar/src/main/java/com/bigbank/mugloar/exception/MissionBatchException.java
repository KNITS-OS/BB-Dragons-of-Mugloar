package com.bigbank.mugloar.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class MissionBatchException extends RuntimeException{

    private String gameId;
    private String message;
}
