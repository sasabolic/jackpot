package com.example.jackpot.adapter.in.rest.dto.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ValidIdValidatorTest {

    private ValidIdValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidIdValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void givenNullOrBlank_whenIsValid_thenFalse(String input) {
        assertThat(validator.isValid(input, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "not-a-uuid",
            "1234",
            "550e8400e29b41d4a716446655440000",
            "550e8400-e29b-41d4-a716-44665544ZZZZ",
            "550e8400-e29b-41d4-a716-44665544000",
            "550e8400-e29b-41d4-a716-4466554400000",
            " 550e8400-e29b-41d4-a716-446655440000 ",
            "550e8400-e29b-41d4-a716-446655440000\n"
    })
    void givenInvalidUuid_whenIsValid_thenFalse(String input) {
        assertThat(validator.isValid(input, context)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00000000-0000-0000-0000-000000000000",
            "550e8400-e29b-41d4-a716-446655440000",
            "550E8400-E29B-41D4-A716-446655440000"
    })
    void givenValidUuid_whenIsValid_thenTrue(String input) {
        assertThat(validator.isValid(input, context)).isTrue();
    }
}