package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;

import java.time.Instant;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

public final class JackpotContribution {

    private final BetId betId;
    private final UserId userId;
    private final JackpotId jackpotId;

    private final Money stakeAmount;
    private final Money contributionAmount;
    private final Money currentJackpotAmount;

    private final Instant createdAt;

    public JackpotContribution(BetId betId, UserId userId, JackpotId jackpotId, Money stakeAmount, Money contributionAmount, Money currentJackpotAmount) {
        this(betId, userId, jackpotId, stakeAmount, contributionAmount, currentJackpotAmount, Instant.now());
    }

    public JackpotContribution(BetId betId, UserId userId, JackpotId jackpotId, Money stakeAmount, Money contributionAmount, Money currentJackpotAmount, Instant createdAt) {
        requireNonNull(betId, "betId must not be null");
        requireNonNull(userId, "userId must not be null");
        requireNonNull(jackpotId, "jackpotId must not be null");
        requireNonNull(stakeAmount, "stakeAmount must not be null");
        requireNonNull(contributionAmount, "contributionAmount must not be null");
        requireNonNull(currentJackpotAmount, "currentJackpotAmount must not be null");
        requireNonNull(createdAt, "createdAt must not be null");

        isTrue(stakeAmount.isPositive(), "stakeAmount must be positive");
        isTrue(contributionAmount.isZero() || contributionAmount.isPositive(), "contributionAmount must be >= 0");
        isTrue(currentJackpotAmount.isZero() || currentJackpotAmount.isPositive(), "currentJackpotAmount must be >= 0");

        isTrue(stakeAmount.hasSameCurrencyAs(contributionAmount) && stakeAmount.hasSameCurrencyAs(currentJackpotAmount), "stakeAmount, contributionAmount, and currentJackpotAmount must be of same currency");
        isTrue(contributionAmount.isLessThan(stakeAmount), "contributionAmount must be less than stakeAmount");

        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.stakeAmount = stakeAmount;
        this.contributionAmount = contributionAmount;
        this.currentJackpotAmount = currentJackpotAmount;
        this.createdAt = createdAt;
    }

    public BetId betId() {
        return this.betId;
    }

    public UserId userId() {
        return this.userId;
    }

    public JackpotId jackpotId() {
        return this.jackpotId;
    }

    public Money stakeAmount() {
        return this.stakeAmount;
    }

    public Money contributionAmount() {
        return this.contributionAmount;
    }

    public Money currentJackpotAmount() {
        return this.currentJackpotAmount;
    }

    public Instant createdAt() {
        return this.createdAt;
    }
}
