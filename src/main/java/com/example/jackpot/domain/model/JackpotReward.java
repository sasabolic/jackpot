package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.CycleNumber;
import com.example.jackpot.domain.model.vo.JackpotCycle;
import com.example.jackpot.domain.model.vo.Money;

import java.time.Instant;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

/**
 * A domain value representing a reward granted to a bet in a specific jackpot cycle.
 */
public final class JackpotReward {

    private final BetId betId;
    private final UserId userId;
    private final JackpotId jackpotId;
    private final CycleNumber jackpotCycle;

    private final Money rewardAmount;
    private final Instant createdAt;

    public JackpotReward(BetId betId, UserId userId, JackpotCycle jackpotCycle, Money rewardAmount) {
        this(betId, userId, jackpotCycle, rewardAmount, Instant.now());
    }

    public JackpotReward(BetId betId, UserId userId, JackpotCycle jackpotCycle, Money rewardAmount, Instant createdAt) {
        requireNonNull(betId, "betId must not be null");
        requireNonNull(userId, "userId must not be null");
        requireNonNull(jackpotCycle, "jackpotCycle must not be null");
        requireNonNull(rewardAmount, "rewardAmount must not be null");
        requireNonNull(createdAt, "createdAt must not be null");

        isTrue(rewardAmount.isPositive(), "rewardAmount must be positive");

        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotCycle.jackpotId();
        this.jackpotCycle = jackpotCycle.cycle();
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

    public CycleNumber jackpotCycle() {
        return this.jackpotCycle;
    }

    public Money rewardAmount() {
        return this.rewardAmount;
    }

    public Instant createdAt() {
        return this.createdAt;
    }
}
