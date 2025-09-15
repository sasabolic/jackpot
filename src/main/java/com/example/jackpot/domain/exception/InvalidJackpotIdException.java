package com.example.jackpot.domain.exception;

public class InvalidJackpotIdException extends RuntimeException {

    public InvalidJackpotIdException(String message) {
        super(message);
    }

    public InvalidJackpotIdException(String message, Throwable cause) {
        super(message, cause);
    }
}