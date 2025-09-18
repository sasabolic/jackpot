package com.example.jackpot.adapter.in.rest.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class BetRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void givenValidRequest_whenValidate_thenNoViolations() {
        BetRequest dto = new BetRequest(
                validBetId(),
                validUserId(),
                validJackpotId(),
                validBetAmount()
        );

        Set<ConstraintViolation<BetRequest>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    void givenAllParametersAreNull_whenValidate_thenViolationsForAllParameters() {
        BetRequest dto = new BetRequest(
                null,
                null,
                null,
                null
        );

        Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

        assertThat(result)
                .isNotEmpty()
                .hasSize(4);
    }

    // ----------------------------------
    // BetId Tests
    // ----------------------------------

    @Nested
    class BetIdTests {

        @ParameterizedTest(name = "Invalid betId=\"{0}\" should cause violation")
        @NullSource
        @ValueSource(strings = {"", " ", "\t", "\n", "    "})
        void givenBlankBetId_whenValidate_thenViolationForBetId(String betId) {
            BetRequest dto = new BetRequest(
                    betId,
                    validUserId(),
                    validJackpotId(),
                    validBetAmount()
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("betId", "must not be blank"));
        }
    }

    // ----------------------------------
    // UserId Tests
    // ----------------------------------

    @Nested
    class UserIdTests {

        @ParameterizedTest(name = "Invalid userId=\"{0}\" should cause violation")
        @NullSource
        @ValueSource(strings = {"", " ", "\t", "\n", "    "})
        void givenBlankUserId_whenValidate_thenViolationForUserId(String userId) {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    userId,
                    validJackpotId(),
                    validBetAmount()
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("userId", "must not be blank"));
        }
    }

    // ----------------------------------
    // JackpotId Tests
    // ----------------------------------

    @Nested
    class JackpotIdTests {

        @ParameterizedTest(name = "Invalid jackpotId=\"{0}\" should cause violation")
        @NullSource
        @ValueSource(strings = {"", " ", "\t", "\n", "    "})
        void givenBlankJackpotId_whenValidate_thenViolationForJackpotId(String jackpotId) {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    validUserId(),
                    jackpotId,
                    validBetAmount()
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("jackpotId", "must not be blank"));
        }
    }

    // ----------------------------------
    // BetAmount Tests
    // ----------------------------------

    @Nested
    class BetAmountTests {

        @Test
        void givenBetAmountIsNull_whenValidate_thenViolationForBetAmount() {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    validUserId(),
                    validJackpotId(),
                    null
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("betAmount", "must not be null"));
        }

        @ParameterizedTest(name = "Invalid amount=\"{0}\" should cause violation")
        @NullSource
        @ValueSource(strings = {"", " ", "\t", "\n", "    "})
        void givenBlankAmount_whenValidate_thenViolationForBetAmount(String amount) {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    validUserId(),
                    validJackpotId(),
                    new MoneyDto(amount, "EUR")
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("betAmount.amount", "must not be blank"));
        }

        @ParameterizedTest(name = "Invalid amount=\"{0}\" should cause violation")
        @ValueSource(strings = {"0", "-0.01"})
        void givenInvalidAmount_whenValidate_thenViolationForBetAmount(String betAmount) {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    validUserId(),
                    validJackpotId(),
                    new MoneyDto(betAmount, "EUR")
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("betAmount.amount", "amount must be a positive number with up to 2 decimals"));
        }

        @Test
        void givenNullCurrency_whenValidate_thenViolationForBetAmount() {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    validUserId(),
                    validJackpotId(),
                    new MoneyDto("10.00", null)
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("betAmount.currency", "must not be null"));
        }

        @ParameterizedTest(name = "Invalid currency=\"{0}\" should cause violation")
        @ValueSource(strings = {"", " ", "\t", "\n", "    ", "eur", "eu", "Euro"})
        void givenInvalidCurrency_whenValidate_thenViolationForBetAmount(String currency) {
            BetRequest dto = new BetRequest(
                    validBetId(),
                    validUserId(),
                    validJackpotId(),
                    new MoneyDto("10.00", currency)
            );

            Set<ConstraintViolation<BetRequest>> result = validator.validate(dto);

            assertThat(result)
                    .isNotEmpty()
                    .hasSize(1)
                    .extracting(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage
                    )
                    .containsExactly(tuple("betAmount.currency", "currency must be a 3-letter ISO code"));
        }
    }

    // ----------------------------------
    // Fixtures
    // ----------------------------------

    private String validBetId() {
        return UUID.randomUUID().toString();
    }

    private String validUserId() {
        return UUID.randomUUID().toString();
    }

    private String validJackpotId() {
        return UUID.randomUUID().toString();
    }

    private MoneyDto validBetAmount() {
        return new MoneyDto("10.00", "EUR");
    }
}