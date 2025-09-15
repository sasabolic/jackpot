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
    void givenNullStartPercent_whenNewInstance_thenThrowException() {
        Percentage startPercent = null;
        Money rewardPoolLimit = Money.of("100.00", "EUR");

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(startPercent, rewardPoolLimit))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("startPercent must not be null");
    }

    @Test
    void givenNullRewardPoolLimit_whenNewInstance_thenThrowException() {
        Percentage startPercent = Percentage.of("1.00");
        Money rewardPoolLimit = null;

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(startPercent, rewardPoolLimit))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("rewardPoolLimit must not be null");
    }

    @Test
    void givenNullRandomNumberGenerator_whenNewInstance_thenThrowException() {
        Percentage startPercent = Percentage.of("1.00");
        Money rewardPoolLimit = Money.of("100.00", "EUR");
        DoubleSupplier randomNumberGenerator = null;

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(startPercent, rewardPoolLimit, randomNumberGenerator))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("randomNumberGenerator must not be null");
    }

    @Test
    void givenStartPercentageIs100_whenNewInstance_thenThrowException() {
        Percentage startPercent = Percentage.of("100.00");
        Money rewardPoolLimit = Money.of("100.00", "EUR");

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(startPercent, rewardPoolLimit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startPercent must be < 100%");
    }

    @ParameterizedTest(name = "[{index}] reward pool limit={0} → IllegalArgumentException (must be > 0)")
    @CsvSource({"0.00", "-0.01"})
    void givenNonPositiveCap_whenNewInstance_thenThrowException(String rewardPoolLimitAmount) {
        Percentage startPercent = Percentage.of("1.00");
        Money rewardPoolLimit = Money.of(rewardPoolLimitAmount, "EUR");

        assertThatThrownBy(() -> new VariableChanceRewardEvaluator(startPercent, rewardPoolLimit))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("rewardPoolLimit must be > 0");
    }

    @Test
    void whenNewInstance_thenFieldsAreWired() {
        VariableChanceRewardEvaluator result = new VariableChanceRewardEvaluator(Percentage.of("5.00"), Money.of("250.00", "EUR"));

        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("startPercent", Percentage.of("5.00"))
                .hasFieldOrPropertyWithValue("rewardPoolLimit", Money.of("250.00", "EUR"));
    }

    // ----------------------------
    // Evaluation Tests
    // ----------------------------

    @Test
    void givenNullContext_whenEvaluate_thenThrowException() {
        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(Percentage.of("5.00"), Money.of("100.00", "EUR"));

        assertThatThrownBy(() -> evaluator.evaluate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ctx must not be null");
    }

    @Test
    void givenCurrencyMismatch_whenEvaluate_thenThrowException() {
        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(Percentage.of("10.00"), Money.of("100.00", "USD"));
        RewardContext ctx = new RewardContext(Money.of("10.00", "EUR"));

        assertThatThrownBy(() -> evaluator.evaluate(ctx))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Jackpot current pool and reward pool limit must have same currency");
    }

    @ParameterizedTest(name = "[{index}] current={0} EUR, cap={1} EUR → win=true (RNG not called)")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    CURRENT_POOL, REWARD_POOL_LIMIT
                    100.00,       100.00
                    150.00,       100.00
                    100.01,       100.00
                    """
    )
    void givenAtOrAboveCap_whenEvaluate_thenAlwaysTrueAndNoRng(String currentPoolAmount, String rewardPoolLimitAmount) {
        DoubleSupplier rng = () -> {
            throw new AssertionError("RNG must not be called at/above cap");
        };

        Money cap = Money.of(rewardPoolLimitAmount, "EUR");
        VariableChanceRewardEvaluator evaluator =
                new VariableChanceRewardEvaluator(Percentage.of("5.00"), cap, rng);

        boolean result = evaluator.evaluate(new RewardContext(Money.of(currentPoolAmount, "EUR")));

        assertThat(result).isTrue();
    }


    @ParameterizedTest(name = "[{index}] start={0}% cap={1} current={2} rng={3} → win={4}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    START_PERCENTAGE,  REWARD_POOL_LIMIT,   CURRENT_POOL,    GENERATED_RANDOM_NUM,   EXPECTED
                    25.00,             100.00,              50.00,           0.625,                  true
                    25.00,             100.00,              50.00,           0.6251,                 false
                    25.00,             100.00,              50.00,           0.6249,                 true
                    0.00,              100.00,              25.00,           0.25,                   true
                    0.00,              100.00,              25.00,           0.2501,                 false
                    """
    )
    void whenEvaluate_thenLinearChanceCalculatedCorrectly(String startPercentage, String rewardPoolLimitAmount, String currentPoolAmount, String rngVal, boolean expected) {
        DoubleSupplier rng = () -> Double.parseDouble(rngVal);

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(Percentage.of(startPercentage), Money.of(rewardPoolLimitAmount, "EUR"), rng);
        RewardContext ctx = new RewardContext(Money.of(currentPoolAmount, "EUR"));

        boolean result = evaluator.evaluate(ctx);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void givenStartPercentIs0_whenEvaluate_thenLosses() {
        DoubleSupplier rng = () -> 1.0;

        VariableChanceRewardEvaluator evaluator = new VariableChanceRewardEvaluator(Percentage.of("0"), Money.of("100.00", "EUR"), rng);
        RewardContext ctx = new RewardContext(Money.of("99.99", "EUR"));

        boolean result = evaluator.evaluate(ctx);

        assertThat(result).isFalse();
    }
}