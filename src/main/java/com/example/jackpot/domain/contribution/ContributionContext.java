package com.example.jackpot.domain.contribution;


import com.example.jackpot.domain.model.vo.Money;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

/**
 * Input data for jackpot contribution strategies.
 *
 * @param betAmount   the stake placed by the player
 * @param currentPool the jackpot pool before applying this bet
 * @param initialPool the pool at the start of the current cycle
 */
public record ContributionContext(Money betAmount, Money currentPool, Money initialPool) {
    public ContributionContext {
        requireNonNull(betAmount, "betAmount must not be null");
        requireNonNull(currentPool, "currentPool must not be null");
        requireNonNull(initialPool, "initialPool must not be null");

        isTrue(betAmount.isPositive(), "betAmount must be > 0");
        isTrue(betAmount.hasSameCurrencyAs(currentPool, initialPool), "betAmount, currentPool and initialPool must share the same currency");
        isTrue(currentPool.isGreaterThanOrEqual(initialPool), "currentPool must be ≥ initialPool");
    }
}
