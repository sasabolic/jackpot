package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.function.DoubleSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class FixedChanceRewardEvaluatorTest {

    // ----------------------------------
    // Constructor Tests
    // ----------------------------------

    @Test
    void givenNullChancePercent_whenNewInstance_thenThrowException() {
        assertThatThrownBy(() -> new FixedChanceRewardEvaluator(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("chance must not be null");
    }

    @Test
    void givenNullRandomNumberGenerator_whenNewInstance_thenThrowException() {
        assertThatThrownBy(() -> new FixedChanceRewardEvaluator(Percentage.ZERO, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("randomNumberGenerator must not be null");
    }

    @Test
    void givenInvalidChancePercent_whenNewInstance_thenThrowException() {
        Percentage chance = Percentage.of("0.00");

        assertThatThrownBy(() -> new FixedChanceRewardEvaluator(chance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("chancePercent must be > 0%");
    }

    @Test
    void whenNewInstance_thenCorrectlyCreated() {
        FixedChanceRewardEvaluator result = new FixedChanceRewardEvaluator(Percentage.of("2.50"));

        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("chance", Percentage.of("2.50"));
    }

    // ----------------------------------
    // Evaluation Tests
    // ----------------------------------

    @Test
    void givenNullContext_whenEvaluate_thenThrowException() {
        FixedChanceRewardEvaluator evaluator = new FixedChanceRewardEvaluator(Percentage.of("10.00"));

        assertThatThrownBy(() -> evaluator.evaluate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ctx must not be null");
    }

    @Test
    void givenChancePercentIsHundred_whenEvaluate_thenAlwaysTrueAndNoRandomNumberGeneratorCalled() {
        DoubleSupplier rng = () -> {
            throw new AssertionError("RNG must not be called");
        };

        FixedChanceRewardEvaluator evaluator = new FixedChanceRewardEvaluator(Percentage.of("100.00"), rng);
        RewardContext ctx = new RewardContext(Money.of("100.00", "EUR"));

        boolean result = evaluator.evaluate(ctx);

        assertThat(result).isTrue();
    }

    @ParameterizedTest(name = "[{index}] chance={0}, rng={1} → expected win={2}")
    @CsvSource
            (useHeadersInDisplayName = true,
                    textBlock = """
                            CHANCE_PERCENT, GENERATED_RANDOM_NUM,   EXPECTED
                            62.5,           0.625,                  true
                            62.5,           0.6251,                 false
                            25.0,           0.25,                   true
                            25.0,           0.2499,                 true
                            1.0,            0.01,                   true
                            1.0,            0.0101,                 false
                            """)
    void givenChanceAndRandomNumberGenerator_whenEvaluate_thenExpectedResult(String chance, String rngValue, boolean expected) {
        DoubleSupplier rng = () -> Double.parseDouble(rngValue);
        FixedChanceRewardEvaluator evaluator = new FixedChanceRewardEvaluator(Percentage.of(chance), rng);
        RewardContext ctx = new RewardContext(Money.of("10.00", "EUR"));

        boolean result = evaluator.evaluate(ctx);

        assertThat(result).isEqualTo(expected);
    }
}