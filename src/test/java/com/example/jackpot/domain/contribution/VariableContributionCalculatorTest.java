package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.DecayFactor;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class VariableContributionCalculatorTest {

    // ----------------------------------
    // Constructor Tests
    // ----------------------------------

    @Test
    void givenNullStartingRate_whenNewInstance_thenThrowException() {
        Percentage validMinimumRate = validMinimumRate();
        DecayFactor validDecayFactor = validDecayFactor();

        assertThatThrownBy(() -> new VariableContributionCalculator(null, validMinimumRate, validDecayFactor))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("startingRate must not be null");
    }

    @Test
    void givenNullMinimumRate_whenNewInstance_thenThrowException() {
        Percentage validStartingRate = validStartingRate();
        DecayFactor validDecayFactor = validDecayFactor();

        assertThatThrownBy(() -> new VariableContributionCalculator(validStartingRate, null, validDecayFactor))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("minimumRate must not be null");
    }

    @Test
    void givenNullDecayFactor_whenNewInstance_thenThrowException() {
        Percentage validStartingRate = validStartingRate();
        Percentage validMinimumRate = validMinimumRate();

        assertThatThrownBy(() -> new VariableContributionCalculator(validStartingRate, validMinimumRate, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("decayFactor must not be null");
    }

    @ParameterizedTest(name = "[{index}] start={0}, min={1} → IllegalArgumentException (start must be > min)")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    START_RATE,   MIN_RATE
                    5.00,         5.00
                    4.00,         5.00
                    """
    )
    void givenStartNotGreaterThanMin_whenNewInstance_thenThrowException(String startingRate, String minimumRate) {
        DecayFactor validDecayFactor = validDecayFactor();

        assertThatThrownBy(() -> new VariableContributionCalculator(Percentage.of(startingRate), Percentage.of(minimumRate), validDecayFactor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startingRate must be greater than minimumRate");
    }

    @ParameterizedTest(name = "[{index}] invalid startingRate={0}")
    @CsvSource({
            "0.00",
            "100.00",
    })
    void givenInvalidStartingRateBounds_whenNewInstance_thenThrowException(String startingRate) {
        Percentage validMinimumRate = validMinimumRate();
        DecayFactor validDecayFactor = validDecayFactor();

        assertThatThrownBy(() -> new VariableContributionCalculator(Percentage.of(startingRate), validMinimumRate, validDecayFactor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("startingRate must be greater than 0.00 and less than 100.00");
    }

    @ParameterizedTest(name = "[{index}] invalid minimumRate={0}")
    @CsvSource({
            "0.00",
            "100.00",
    })
    void givenInvalidMinimumRateBounds_whenNewInstance_thenThrowException(String min) {
        Percentage validStartingRate = validStartingRate();
        DecayFactor validDecayFactor = validDecayFactor();

        assertThatThrownBy(() -> new VariableContributionCalculator(validStartingRate, Percentage.of(min), validDecayFactor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("minimumRate must be greater than 0.00 and less than 100.00");
    }

    @Test
    void whenNewInstance_thenCorrectlyCreated() {
        Percentage validStartingRate = validStartingRate();
        Percentage validMinimumRate = validMinimumRate();
        DecayFactor validDecayFactor = validDecayFactor();

        VariableContributionCalculator result = new VariableContributionCalculator(validStartingRate, validMinimumRate, validDecayFactor);

        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("startingRate", Percentage.of("10.00"))
                .hasFieldOrPropertyWithValue("minimumRate", Percentage.of("1.00"))
                .hasFieldOrPropertyWithValue("decayFactor", DecayFactor.of("0.01"));
    }

    // ----------------------------------
    // Calculation Tests
    // ----------------------------------

    @Test
    void givenNullContext_whenCalculate_thenThrowException() {
        VariableContributionCalculator calculator = new VariableContributionCalculator(Percentage.of("10.00"), Percentage.of("2.00"), DecayFactor.of("0.01"));

        assertThatThrownBy(() -> calculator.calculate(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ctx must not be null");
    }

    @ParameterizedTest(name = "[{index}] start={0}% min={1}% decay={2} Δ={3} on bet={4} → contribution={5}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    START_RATE,  MIN_RATE,   DECAY,  DELTA,  BET,    EXPECTED
                    25.00,       5.00,       0.01,   0,      10.00,  2.50
                    25.00,       5.00,       0.01,   100,    10.00,  2.40
                    10.00,       2.00,       0.01,   1000,   50.00,  1.00
                    0.50,        0.10,       0.001,  0,      10.00,  0.05
                    """
    )
    void givenValidInputs_whenCalculate_thenCorrectContribution(String start, String min, String decay, String delta, String bet, String expected) {
        VariableContributionCalculator calculator = new VariableContributionCalculator(
                Percentage.of(start),
                Percentage.of(min),
                DecayFactor.of(decay)
        );

        Money initial = Money.of("100.00", "EUR");
        Money current = initial.plus(Money.of(delta, "EUR"));
        ContributionContext ctx = new ContributionContext(Money.of(bet, "EUR"), current, initial);

        Money result = calculator.calculate(ctx);

        assertThat(result).isEqualTo(Money.of(expected, "EUR"));
    }

    @Test
    void givenFractionalRate_whenCalculate_thenHalfUpRoundingApplies() {
        VariableContributionCalculator calculator = new VariableContributionCalculator(Percentage.of("16.666"), Percentage.of("1.00"), DecayFactor.of("0.001"));
        ContributionContext ctx = new ContributionContext(Money.of("10.00", "EUR"), Money.of("200.00", "EUR"), Money.of("200.00", "EUR")); // Δ=0

        Money result = calculator.calculate(ctx);

        assertThat(result).isEqualTo(Money.of("1.67", "EUR"));
    }

    @Test
    void givenLargeDelta_whenCalculate_thenClampsToMinRate() {
        VariableContributionCalculator calculator = new VariableContributionCalculator(Percentage.of("5.00"), Percentage.of("2.00"), DecayFactor.of("1.00"));

        Money initial = Money.of("0.00", "EUR");
        Money current = Money.of("10.00", "EUR");
        ContributionContext ctx = new ContributionContext(Money.of("100.00", "EUR"), current, initial);

        Money result = calculator.calculate(ctx);

        assertThat(result).isEqualTo(Money.of("2.00", "EUR"));
    }

    // ----------------------------------
    // Fixtures
    // ----------------------------------

    private Percentage validStartingRate() {
        return Percentage.of("10.00");
    }

    private Percentage validMinimumRate() {
        return Percentage.of("1.00");
    }

    private DecayFactor validDecayFactor() {
        return DecayFactor.of("0.01");
    }
}