package com.example.jackpot.domain.exception;

public class JackpotNotFoundException extends RuntimeException {

    public JackpotNotFoundException(String message) {
        super(message);
    }

    public JackpotNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}