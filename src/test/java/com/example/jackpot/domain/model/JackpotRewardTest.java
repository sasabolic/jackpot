package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JackpotRewardTest {

    @Test
    void givenNullBetId_whenNewInstance_thenThrowException() {
        BetId betId = null;
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money rewardAmount = eur("1.00");

        assertThatThrownBy(() -> new JackpotReward(betId, userId, jackpotId, rewardAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("betId must not be null");
    }

    @Test
    void givenNullUserId_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = null;
        JackpotId jackpotId = jackpotId();
        Money rewardAmount = eur("1.00");

        assertThatThrownBy(() -> new JackpotReward(betId, userId, jackpotId, rewardAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("userId must not be null");
    }

    @Test
    void givenNullJackpotId_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = null;
        Money rewardAmount = eur("1.00");

        assertThatThrownBy(() -> new JackpotReward(betId, userId, jackpotId, rewardAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("jackpotId must not be null");
    }

    @Test
    void givenNullRewardAmount_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money rewardAmount = null;

        assertThatThrownBy(() -> new JackpotReward(betId, userId, jackpotId, rewardAmount))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("rewardAmount must not be null");
    }

    @Test
    void givenNullCreatedAt_whenNewInstance_thenThrowException() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money rewardAmount = eur("1.00");
        Instant createdAt = null;

        assertThatThrownBy(() -> new JackpotReward(betId, userId, jackpotId, rewardAmount, createdAt))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("createdAt must not be null");
    }

    @ParameterizedTest(name = "[{index}] amount={0} EUR → IllegalArgumentException (must be positive)")
    @ValueSource(strings = {"0.00", "0", "-0.01", "-10.00", "0.004"})
    void givenNonPositiveRewardAmount_whenNewInstance_thenThrowException(String amount) {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money rewardAmount = eur(amount);

        assertThatThrownBy(() -> new JackpotReward(betId, userId, jackpotId, rewardAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("rewardAmount must be positive");
    }

    @Test
    void givenMinimalPositiveAmount_whenNewInstance_thenSuccess() {
        JackpotReward r = new JackpotReward(betId(), userId(), jackpotId(), eur("0.01"));

        assertThat(r.rewardAmount()).isEqualTo(eur("0.01"));
    }

    @Test
    void whenNewInstance_thenSuccess() {
        BetId betId = betId();
        UserId userId = userId();
        JackpotId jackpotId = jackpotId();
        Money amount = eur("123.45");
        Instant createdAt = Instant.parse("2025-02-02T03:04:05Z");

        JackpotReward result = new JackpotReward(betId, userId, jackpotId, amount, createdAt);

        assertThat(result)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.betId()).isEqualTo(betId);
                    assertThat(r.userId()).isEqualTo(userId);
                    assertThat(r.jackpotId()).isEqualTo(jackpotId);
                    assertThat(r.rewardAmount()).isEqualTo(amount);
                    assertThat(r.createdAt()).isEqualTo(createdAt);
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
