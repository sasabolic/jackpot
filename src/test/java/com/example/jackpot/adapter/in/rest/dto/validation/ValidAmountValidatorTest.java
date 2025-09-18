package com.example.jackpot.adapter.in.rest.dto.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ValidAmountValidatorTest {

    private ValidAmountValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new ValidAmountValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void givenNullOrBlank_whenIsValid_thenTrue(String input) {
        assertThat(validator.isValid(input, context)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0", "0.00",
            "1.", ".5",
            "-1", "+1",
            "1.234", "123.456789",
            "1,000.00",
            " 1.00", "1.00 ", " 1.00 ",
            "1e2", "1E2",
            "abc", "NaN", "WrongAmount"
    })
    void givenInvalidAmounts_whenIsValid_thenFalse(String input) {
        assertThat(validator.isValid(input, context)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1", "1.0", "1.00",
            "9", "99.9", "99.99",
            "0.01", "0.10",
            "001", "001.20",
            "00000000000000000000000000000001.23",
            "1234567890123456789012345678901234567890.12"
    })
    void givenValidAmounts_whenIsValid_thenTrue(String input) {
        assertThat(validator.isValid(input, context)).isTrue();
    }
}