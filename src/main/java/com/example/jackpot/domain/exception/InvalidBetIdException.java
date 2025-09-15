package com.example.jackpot.domain.exception;

public class InvalidBetIdException extends RuntimeException {

    public InvalidBetIdException(String message) {
        super(message);
    }

    public InvalidBetIdException(String message, Throwable cause) {
        super(message, cause);
    }
}