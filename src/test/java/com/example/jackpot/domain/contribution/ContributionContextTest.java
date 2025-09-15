package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContributionContextTest {


    @Test
    void whenNewInstance_thenSuccess() {
        Money validBet = validBet();
        Money validCurrentPool = validCurrentPool();
        Money validInitialPool = validInitialPool();

        ContributionContext ctx = new ContributionContext(validBet, validCurrentPool, validInitialPool);

        assertThat(ctx.betAmount()).isEqualTo(validBet);
        assertThat(ctx.currentPool()).isEqualTo(validCurrentPool);
        assertThat(ctx.initialPool()).isEqualTo(validInitialPool);
    }

    @Test
    void givenNullBetAmount_whenNewInstance_thenThrowException() {
        Money validCurrentPool = validCurrentPool();
        Money validInitialPool = validInitialPool();

        assertThatThrownBy(() -> new ContributionContext(null, validCurrentPool, validInitialPool))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("betAmount must not be null");
    }

    @Test
    void givenNullCurrent_whenNewInstance_thenThrowException() {
        Money validBet = validBet();
        Money validInitialPool = validInitialPool();

        assertThatThrownBy(() -> new ContributionContext(validBet, null, validInitialPool))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("currentPool must not be null");
    }

    @Test
    void givenNullInitial_whenNew_thenThrowException() {
        Money validBet = validBet();
        Money validCurrentPool = validCurrentPool();

        assertThatThrownBy(() -> new ContributionContext(validBet, validCurrentPool, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("initialPool must not be null");
    }

    @ParameterizedTest(name = "[{index}] bet={0} → IllegalArgumentException")
    @ValueSource(strings = {"0.00", "-0.01", "-10.00"})
    void givenInvalidBet_whenNewInstance_thenThrowException(String betAmount) {
        Money bet = Money.of(betAmount, "EUR");
        Money validCurrentPool = validCurrentPool();
        Money validInitialPool = validInitialPool();

        assertThatThrownBy(() -> new ContributionContext(bet, validCurrentPool, validInitialPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("betAmount must be > 0");
    }

    @ParameterizedTest(name = "[{index}] bet={0}, current={1}, initial={2} → currency mismatch → IAE")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    BET_CURRENCY,   CURRENT_POOL_CURRENCY,  INITIAL_POOL_CURRENCY
                    EUR,            USD,                    EUR
                    EUR,            EUR,                    USD
                    USD,            EUR,                    EUR
                    EUR,            USD,                    USD
                    USD,            EUR,                    USD
                    USD,            USD,                    EUR
                    """
    )
    void givenCurrencyMismatch_whenNewInstance_thenThrowException(String betCurrency, String currentPoolCurrency, String initialPoolCurrency) {
        Money bet = Money.of("10.00", betCurrency);
        Money currentPool = Money.of("100.00", currentPoolCurrency);
        Money initialPool = Money.of("100.00", initialPoolCurrency);

        assertThatThrownBy(() -> new ContributionContext(bet, currentPool, initialPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("betAmount, currentPool and initialPool must share the same currency");
    }


    @Test
    void givenCurrentPoolIsLessThanInitialPool_whenNewInstance_thenThrowException() {
        Money bet = Money.of("10.00", "EUR");
        Money initialPool = Money.of("100.00", "EUR");
        Money currentPool = Money.of("99.99", "EUR"); // current < initial

        assertThatThrownBy(() -> new ContributionContext(bet, currentPool, initialPool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("currentPool must be ≥ initialPool");
    }

    @Test
    void givenCurrentPoolEqualsInitialPool_whenNewInstance_thenSuccess() {
        Money bet = Money.of("10.00", "EUR");
        Money initial = Money.of("100.00", "EUR");
        Money current = Money.of("100.00", "EUR"); // equal

        ContributionContext ctx = new ContributionContext(bet, current, initial);

        assertThat(ctx).isNotNull();
    }

    // ----------------------------------
    // Fixtures
    // ----------------------------------

    private Money validBet() {
        return Money.of("10.00", "EUR");
    }

    private Money validCurrentPool() {
        return Money.of("105.00", "EUR");
    }

    private Money validInitialPool() {
        return Money.of("100.00", "EUR");
    }
}