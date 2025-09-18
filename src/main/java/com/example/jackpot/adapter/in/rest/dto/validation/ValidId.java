package com.example.jackpot.adapter.in.rest.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidIdValidator.class)
public @interface ValidId {
    String message() default "must be a valid UUID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
