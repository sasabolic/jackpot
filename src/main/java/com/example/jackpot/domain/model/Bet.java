package com.example.jackpot.domain.model;

import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

/**
 * A domain entity representing a placed bet.
 */
public final class Bet {
    private final BetId betId;
    private final UserId userId;
    private final JackpotId jackpotId;
    private final Money betAmount;

    public Bet(BetId betId, UserId userId, JackpotId jackpotId, Money betAmount) {
        requireNonNull(betId, "betId must not be null");
        requireNonNull(userId, "userId must not be null");
        requireNonNull(jackpotId, "jackpotId must not be null");
        requireNonNull(betAmount, "betAmount must not be null");

        isTrue(betAmount.isPositive(), "betAmount must be positive");

        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.betAmount = betAmount;
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

    public Money betAmount() {
        return this.betAmount;
    }
}