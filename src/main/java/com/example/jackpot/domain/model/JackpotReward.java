package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;

import java.time.Instant;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

public final class JackpotReward {

    private final BetId betId;
    private final UserId userId;
    private final JackpotId jackpotId;

    private final Money rewardAmount;
    private final Instant createdAt;

    public JackpotReward(BetId betId, UserId userId, JackpotId jackpotId, Money rewardAmount) {
        this(betId, userId, jackpotId, rewardAmount, Instant.now());
    }

    public JackpotReward(BetId betId, UserId userId, JackpotId jackpotId, Money rewardAmount, Instant createdAt) {
        requireNonNull(betId, "betId must not be null");
        requireNonNull(userId, "userId must not be null");
        requireNonNull(jackpotId, "jackpotId must not be null");
        requireNonNull(rewardAmount, "rewardAmount must not be null");
        requireNonNull(createdAt, "createdAt must not be null");

        isTrue(rewardAmount.isPositive(), "rewardAmount must be positive");

        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.rewardAmount = rewardAmount;
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

    public Money rewardAmount() {
        return this.rewardAmount;
    }

    public Instant createdAt() {
        return this.createdAt;
    }
}
