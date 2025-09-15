package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Percentage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.ZERO;
import static java.util.Objects.requireNonNull;

/**
 * Evaluates a jackpot win using a <b>fixed probability</b> expressed as a {@link Percentage}.
 */
public final class FixedChanceRewardEvaluator implements RewardEvaluator {
    private final Percentage chance;
    private final DoubleSupplier randomNumberGenerator;

    public FixedChanceRewardEvaluator(Percentage chance) {
        this(chance, ThreadLocalRandom.current()::nextDouble);
    }

    public FixedChanceRewardEvaluator(Percentage chance, DoubleSupplier randomNumberGenerator) {
        requireNonNull(chance, "chance must not be null");
        requireNonNull(randomNumberGenerator, "randomNumberGenerator must not be null");

        isTrue(chance.isGreaterThan(ZERO), "chancePercent must be > 0%");

        this.chance = chance;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public boolean evaluate(RewardContext ctx) {
        requireNonNull(ctx, "ctx must not be null");

        if (chance.isHundred()) {
            return true;
        }

        return randomNumberGenerator.getAsDouble() <= chance.fractionalValue().doubleValue();
    }
}
