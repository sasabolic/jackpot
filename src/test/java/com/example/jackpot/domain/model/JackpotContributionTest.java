package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JackpotContributionTest {

    @Test
    void givenNullBetId_whenNewInstance_thenThrowException() {
        BetId betId = null;
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("betId must not be null");
    }

    @Test
    void givenNullUserId_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = null;
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("userId must not be null");
    }

    @Test
    void givenNullJackpotId_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = null;
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("jackpotId must not be null");
    }

    @Test
    void givenNullStakeAmount_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = null;
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("stakeAmount must not be null");
    }

    @Test
    void givenNullContributionAmount_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = null;
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("contributionAmount must not be null");
    }

    @Test
    void givenNullCurrentJackpotAmount_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = null;

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("currentJackpotAmount must not be null");
    }

    @Test
    void givenNullCreatedAt_whenFullCtor_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("11.00");
        Instant createdAt = null;

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount, createdAt))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("createdAt must not be null");
    }

    @ParameterizedTest(name = "[{index}] stake={0} EUR → IllegalArgumentException (stakeAmount must be positive)")
    @CsvSource({"0.00", "-0.01"})
    void givenNonPositiveStake_whenNewInstance_thenThrowException(String stake) {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur(stake);
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stakeAmount must be positive");
    }

    @Test
    void givenNegativeContribution_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("-0.01");
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("contributionAmount must be >= 0");
    }

    @Test
    void givenNegativeCurrentPool_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("-0.01");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("currentJackpotAmount must be >= 0");
    }

    @ParameterizedTest(name = "[{index}] stake={0} contrib={1} → IAE (contribution < stake required)")
    @CsvSource({
            "10.00, 10.00",
            "10.00, 12.00"
    })
    void givenContributionNotLessThanStake_whenNewInstance_thenThrowException(String stake, String contribution) {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur(stake);
        Money contributionAmount = eur(contribution);
        Money currentJackpotAmount = eur("11.00");

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("contributionAmount must be less than stakeAmount");
    }

    @Test
    void givenZeroContribution_whenNewInstance_thenSuccess() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("0.00");
        Money currentJackpotAmount = eur("11.00");

        JackpotContribution result = new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount);

        assertThat(result.contributionAmount()).isEqualTo(eur("0.00"));
    }

    @Test
    void givenZeroCurrentJackpot_whenNewInstance_thenSuccess() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = eur("10.00");
        Money contributionAmount = eur("1.00");
        Money currentJackpotAmount = eur("0.00");

        JackpotContribution result = new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount);

        assertThat(result.currentJackpotAmount()).isEqualTo(eur("0.00"));
    }

    @ParameterizedTest(name = "[{index}] stake={0}, contribution={1}, current={2} → currency mismatch → IAE")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    STAKE_CURRENCY, CONTRIBUTION_CURRENCY, CURRENT_JACKPOT_CURRENCY
                    EUR,            USD,                   EUR
                    EUR,            EUR,                   USD
                    USD,            EUR,                   EUR
                    EUR,            USD,                   USD
                    USD,            EUR,                   USD
                    USD,            USD,                   EUR
                    """
    )
    void givenCurrencyMismatch_whenNewInstance_thenThrowException(String stakeCurrency, String contributionCurrency, String currentJackpotCurrency) {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stakeAmount = Money.of("10.00", stakeCurrency);
        Money contributionAmount = Money.of("1.00", contributionCurrency);
        Money currentJackpotAmount = Money.of("11.00", currentJackpotCurrency);

        assertThatThrownBy(() -> new JackpotContribution(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("stakeAmount, contributionAmount, and currentJackpotAmount must be of same currency");
    }

    @Test
    void whenNewInstance_thenSuccess() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money stake = eur("10.00");
        Money contribution = eur("1.00");
        Money current = eur("11.00");
        Instant createdAt = Instant.parse("2025-02-02T03:04:05Z");

        JackpotContribution result = new JackpotContribution(betId, userId, jackpotId, stake, contribution, current, createdAt);

        assertThat(result)
                .isNotNull()
                .satisfies(jc -> {
                    assertThat(jc.betId()).isEqualTo(betId);
                    assertThat(jc.userId()).isEqualTo(userId);
                    assertThat(jc.jackpotId()).isEqualTo(jackpotId);
                    assertThat(jc.stakeAmount()).isEqualTo(stake);
                    assertThat(jc.contributionAmount()).isEqualTo(contribution);
                    assertThat(jc.currentJackpotAmount()).isEqualTo(current);
                    assertThat(jc.createdAt()).isEqualTo(createdAt);
                });
    }

    // ----------------------------------
    // Fixtures / Helpers
    // ----------------------------------

    private static BetId betId() {
        return BetId.of(UUID.randomUUID());
    }

    private static UserId userId() {
        return UserId.of(UUID.randomUUID());
    }

    private static JackpotId jackpotId() {
        return JackpotId.of(UUID.randomUUID());
    }

    private static Money eur(String amount) {
        return Money.of(amount, "EUR");
    }
}
