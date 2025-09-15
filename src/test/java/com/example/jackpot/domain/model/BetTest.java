package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BetTest {

    @Test
    void givenNullBetId_whenNewInstance_thenThrowException() {
        BetId betId = null;
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money betAmount = eur("1.00");

        assertThatThrownBy(() -> new Bet(betId, userId, jackpotId, betAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("betId must not be null");
    }

    @Test
    void givenNullUserId_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = null;
        JackpotId jackpotId = jackpotId();
        Money betAmount = eur("1.00");

        assertThatThrownBy(() -> new Bet(betId, userId, jackpotId, betAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("userId must not be null");
    }

    @Test
    void givenNullJackpotId_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = null;
        Money betAmount = eur("1.00");

        assertThatThrownBy(() -> new Bet(betId, userId, jackpotId, betAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("jackpotId must not be null");
    }

    @Test
    void givenNullBetAmount_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money betAmount = null;

        assertThatThrownBy(() -> new Bet(betId, userId, jackpotId, betAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("betAmount must not be null");
    }

    @Test
    void whenNewInstance_thenCorrectlyCreated() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money amount = eur("10.00");

        Bet bet = new Bet(betId, userId, jackpotId, amount);

        assertThat(bet).isNotNull()
                .satisfies(b -> {
                    assertThat(bet.betId()).isEqualTo(betId);
                    assertThat(bet.userId()).isEqualTo(userId);
                    assertThat(bet.jackpotId()).isEqualTo(jackpotId);
                    assertThat(bet.betAmount()).isEqualTo(amount);
                });
    }

    @ParameterizedTest(name = "[{index}] amount={0} EUR → IllegalArgumentException (must be positive)")
    @ValueSource(strings = {"0.00", "0", "-0.01", "-10.00", "0.004"})
    void givenNonPositiveAmount_whenNewInstance_thenThrowException(String amount) {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money betAmount = eur(amount);

        assertThatThrownBy(() -> new Bet(betId, userId, jackpotId, betAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("betAmount must be positive");
    }

    @Test
    void givenMinimalPositiveAmount_whenNewInstance_thenOk() {
        Bet bet = new Bet(betId(), userId(), jackpotId(), eur("0.01"));

        assertThat(bet.betAmount()).isEqualTo(eur("0.01"));
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