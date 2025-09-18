package com.example.jackpot.adapter.in.rest.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class ValidAmountValidator implements ConstraintValidator<ValidAmount, String> {

    private static final Pattern REGEX = Pattern.compile("\\d+(?:\\.\\d{1,2})?");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        if (!REGEX.matcher(value).matches()) {
            return false;
        }

        try {
            return new BigDecimal(value).compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
