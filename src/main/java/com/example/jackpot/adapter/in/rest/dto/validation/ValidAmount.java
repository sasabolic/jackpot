package com.example.jackpot.adapter.in.rest.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAmountValidator.class)
public @interface ValidAmount {
    String message() default "amount must be a positive number with up to 2 decimals";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
