package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FixedContributionCalculatorTest {

    // ----------------------------------
    // Constructor Tests
    // ----------------------------------

    @Test
    void givenNullRate_whenNewInstance_thenThrowException() {
        assertThatThrownBy(() -> new FixedContributionCalculator(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("rate must not be null");
    }

    @ParameterizedTest(name = "[{index}] given rate={0} → IllegalArgumentException (must be 0<rate<100)")
    @CsvSource({
            "0.00",
            "100.00",
    })
    void givenInvalidRate_whenNewInstance_thenThrowException(String value) {
        Percentage rate = Percentage.of(value);

        assertThatThrownBy(() -> new FixedContributionCalculator(rate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("rate must be between 0.00 and 100.00");
    }

    @Test
    void whenNewInstance_thenCorrectlyCreated() {
        FixedContributionCalculator result = new FixedContributionCalculator(Percentage.of("1.00"));

        assertThat(result).isNotNull()
                .hasFieldOrPropertyWithValue("rate", Percentage.of("1.00"));
    }

    // ----------------------------------
    // Calculation Tests
    // ----------------------------------

    @Test
    void givenNullContext_whenCalculate_thenThrowException() {
        var calc = new FixedContributionCalculator(Percentage.of("10.00"));

        assertThatThrownBy(() -> calc.calculate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ctx must not be null");
    }

    @ParameterizedTest(name = "[{index}] given bet={0} and rate={1}% → contribution={2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    RATE,       BET_AMOUNT,    EXPECTED
                    25.00,      10.00,         2.50
                    12.34,      123.45,        15.23
                    0.50,       10.00,         0.05
                    99.99,      10.00,         10.00
                    """
    )
    void givenValidInputs_whenCalculate_thenCorrectContribution(String rate, String betAmount, String expected) {
        var calc = new FixedContributionCalculator(Percentage.of(rate));
        var ctx = new ContributionContext(Money.of(betAmount, "EUR"), Money.of("0.00", "EUR"), Money.of("0.00", "EUR"));

        Money result = calc.calculate(ctx);

        assertThat(result).isEqualTo(Money.of(expected, "EUR"));
    }

    @Test
    void givenTinyRate_whenCalculate_thenMayBeZero() {
        var calc = new FixedContributionCalculator(Percentage.of("0.01")); // 0.01%
        var ctx = new ContributionContext(Money.of("10.00", "EUR"), Money.of("0.00", "EUR"), Money.of("0.00", "EUR"));

        Money contribution = calc.calculate(ctx);

        assertThat(contribution).isEqualTo(Money.of("0.00", "EUR"));
    }

    @Test
    void givenFractionalRate_whenCalculate_thenHalfUpRoundingApplies() {
        var calc = new FixedContributionCalculator(Percentage.of("16.666"));
        var ctx = new ContributionContext(Money.of("10.00", "EUR"), Money.of("0.00", "EUR"), Money.of("0.00", "EUR"));

        Money result = calc.calculate(ctx);

        assertThat(result).isEqualTo(Money.of("1.67", "EUR"));
    }
}