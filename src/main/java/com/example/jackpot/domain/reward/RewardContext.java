package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Money;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

/**
 * Input data for reward evaluation strategies.
 *
 * @param currentPool the jackpot current pool at the moment of evaluation
 */
public record RewardContext(Money currentPool) {
    public RewardContext {
        requireNonNull(currentPool, "currentPool must not be null");

        isTrue(currentPool.isPositive(), "currentPool must be > 0");
    }
}
