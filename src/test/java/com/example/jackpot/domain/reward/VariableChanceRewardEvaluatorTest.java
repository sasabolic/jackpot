package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.function.DoubleSupplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VariableChanceRewardEvaluatorTest {

    // ----------------------------
    // Constructor Tests
    // ----------------------------

    @Test
    void givenNullMinPercent_whenNewInstance_thenThrowException() {
        Percentage minPercent = null;
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minPercent must not be null");
    }

    @Test
    void givenNullMaxPercent_whenNewInstance_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = null;
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maxPercent must not be null");
    }

    @Test
    void givenNullminPool_whenNewInstance_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = null;
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minPool must not be null");
    }

    @Test
    void givenNullmaxPool_whenNewInstance_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = null;

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("maxPool must not be null");
    }

    @Test
    void givenNullRandomNumberGenerator_whenNewInstance_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();
        DoubleSupplier randomNumberGenerator = null;

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool, randomNumberGenerator))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("randomNumberGenerator must not be null");
    }

    @Test
    void givenMinPercentIs100_whenNewInstance_thenThrowException() {
        Percentage minPercent = Percentage.HUNDRED;
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minPercent must be < 100%");
    }

    @Test
    void givenMinPercentIsEqualToMaxPercent_whenNewInstance_thenThrowException() {
        Percentage minPercent = Percentage.of("2.00");
        Percentage maxPercent = Percentage.of("2.00");
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minPercent must be < maxPercent");
    }

    @Test
    void givenMinPercentIsGreaterThanMaxPercent_whenNewInstance_thenThrowException() {
        Percentage minPercent = Percentage.of("2.01");
        Percentage maxPercent = Percentage.of("2.00");
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minPercent must be < maxPercent");
    }

    @ParameterizedTest(name = "[{index}] minPool={0} → IllegalArgumentException (must be > 0)")
    @CsvSource({"0.00", "-0.01"})
    void givenNonPositiveMinPool_whenNewInstance_thenThrowException(String minPoolAmount) {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = Money.of(minPoolAmount, "EUR");
        Money maxPool = validMaxPool();

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minPool must be > 0");
    }

    @ParameterizedTest(name = "[{index}] maxPool={0} → IllegalArgumentException (must be > 0)")
    @CsvSource({"0.00", "-0.01"})
    void givenNonPositiveMaxPool_whenNewInstance_thenThrowException(String maxPoolAmount) {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = Money.of(maxPoolAmount, "EUR");

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxPool must be > 0");
    }

    @Test
    void givenMinPoolAndMaxPoolCurrencyMismatch_whenNewInstance_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = Money.of("50.00", "EUR");
        Money maxPool = Money.of("100.00", "USD");

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minPool and maxPool must share the same currency");
    }

    @Test
    void givenMaxPoolIsLessThanMinPool_whenNewInstance_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = Money.of("100.01", "EUR");
        Money maxPool = Money.of("100.00", "EUR");

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxPool must be >= minPool");
    }

    @Test
    void whenNewInstance_thenFieldsAreWired() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        VariableChanceRewardEvaluator result = new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool);

        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("minPercent", Percentage.of("2.00"))
                .hasFieldOrPropertyWithValue("maxPercent", Percentage.of("100.00"))
                .hasFieldOrPropertyWithValue("minPool", Money.of("50.00", "EUR"))
                .hasFieldOrPropertyWithValue("maxPool", Money.of("100.00", "EUR"));
    }

    // ----------------------------
    // Evaluation Tests
    // ----------------------------

    @Test
    void givenNullContext_whenEvaluate_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool);

        assertThatThrownBy(() -> evaluator.evaluate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ctx must not be null");
    }

    @Test
    void givenCurrencyMismatch_whenEvaluate_thenThrowException() {
        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool);

        RewardContext ctx = new RewardContext(Money.of("10.00", "USD"));

        assertThatThrownBy(() -> evaluator.evaluate(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Jackpot currentPool, minPool and maxPool must have same currency");
    }

    @ParameterizedTest(name = "[{index}] min%={0}, max%={1}, minAmt={2}, maxAmt={3}, current={4}, rng={5} -> expected={6}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    MIN_PERCENT,    MAX_PERCENT,    MIN_POOL,   MAX_POOL,   CURRENT_POOL,   RNG,        EXPECTED
                    # --- at/above max: always true (rng ignored) ---
                    10,             60,             100,        200,        200,            0.999,      true
                    10,             60,             100,        200,        250,            0.000,      true
                    
                    # --- at/below min: STRICT '<' on minPercent (12.5% => 0.125) ---
                    12.5,           60,             100,        200,        100,            0.1249999,  true
                    12.5,           60,             100,        200,        100,            0.125,      false
                    12.5,           60,             100,        200,        50,             0.1249999,  true
                    12.5,           60,             100,        200,        50,             0.125,      false
                    
                    # --- between min & max: interpolated with '<=' ---
                    # min% 10, max% 60, min=100, max=200 -> at 150: chance = 35%
                    10,             60,             100,        200,        150,            0.35,       false
                    10,             60,             100,        200,        150,            0.3499999,  true
                    
                    # --- rounding check (scale=10, DOWN) ---
                    # current=133 -> ratio 33/100 = 0.33, chance = 26.5% (0.265)
                    10,             60,             100,        200,        133,            0.265,      false
                    10,             60,             100,        200,        133,            0.2649999,  true
                    """
    )
    void whenEvaluate_thenChanceCalculatedCorrectly(String minPercentValue, String maxPercentValue, String minPoolAmount, String maxPoolAmount, String currentPoolAmount, double rngValue, boolean expected) {
        Percentage minPercent = Percentage.of(minPercentValue);
        Percentage maxPercent = Percentage.of(maxPercentValue);
        Money minPool = Money.of(minPoolAmount, "EUR");
        Money maxPool = Money.of(maxPoolAmount, "EUR");

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool, () -> rngValue);

        boolean result = evaluator.evaluate(new RewardContext(Money.of(currentPoolAmount, "EUR")));

        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{index}] current={0} EUR, cap={1} EUR → win=true (RNG not called)")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    CURRENT_POOL, MAX_POOL
                    100.00,       100.00
                    150.00,       100.00
                    100.01,       100.00
                    """
    )
    void givenAtOrAboveCap_whenEvaluate_thenAlwaysTrueAndNoRng(String currentPoolAmount, String maxPoolAmount) {
        DoubleSupplier rng = () -> {
            throw new AssertionError("RNG must not be called at/above cap");
        };

        Percentage minPercent = validMinPercent();
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = Money.of(maxPoolAmount, "EUR");

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool, rng);

        boolean result = evaluator.evaluate(new RewardContext(Money.of(currentPoolAmount, "EUR")));

        assertThat(result).isTrue();
    }

    @Test
    void givenMinPercentIs0_whenEvaluate_thenLosses() {
        DoubleSupplier rng = () -> 1.0;

        Percentage minPercent = Percentage.ZERO;
        Percentage maxPercent = validMaxPercent();
        Money minPool = validMinPool();
        Money maxPool = validMaxPool();

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(minPercent, maxPercent, minPool, maxPool, rng);
        RewardContext ctx = new RewardContext(Money.of("99.99", "EUR"));

        boolean result = evaluator.evaluate(ctx);

        assertThat(result).isFalse();
    }

    // ----------------------------------
    // Fixtures
    // ----------------------------------

    private Percentage validMinPercent() {
        return Percentage.of("2.00");
    }

    private Percentage validMaxPercent() {
        return Percentage.HUNDRED;
    }

    private Money validMinPool() {
        return Money.of("50.00", "EUR");
    }

    private Money validMaxPool() {
        return Money.of("100.00", "EUR");
    }
}