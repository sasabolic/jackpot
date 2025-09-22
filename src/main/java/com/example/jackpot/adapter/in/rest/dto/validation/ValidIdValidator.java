package com.example.jackpot.adapter.in.rest.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class ValidIdValidator implements ConstraintValidator<ValidId, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        int length = value.length();
        if (length != 36) {
            return false;
        }

        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to parse value {}", value, ex);
            return false;
        }
    }
}
