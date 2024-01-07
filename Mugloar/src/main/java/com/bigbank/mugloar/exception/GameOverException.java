package com.bigbank.mugloar.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class GameOverException extends RuntimeException{
    public GameOverException(String message, Throwable cause) {
        super(message, cause);
    }
    public GameOverException(String message) {
        super(message);
    }


}
