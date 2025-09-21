package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Percentage;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.ZERO;
import static java.util.Objects.requireNonNull;

/**
 * Evaluates a jackpot win using a <b>fixed probability</b> expressed as a {@link Percentage}.
 *
 * <p><b>Formula:</b></p>
 * <pre>
 * win = (random number) < chance
 * </pre>
 *
 * <p>Where:</p>
 * <ul>
 *   <li>{@code chance} is a fixed {@link Percentage} between 0% and 100%</li>
 *   <li>{@code random number} is a double in the range [0.0, 1.0), supplied by {@link DoubleSupplier}</li>
 * </ul>
 *
 * <p>If {@code chance} is 100%, the outcome is guaranteed to win.
 * Otherwise, the evaluator compares a random number against the fractional value of {@code chance}.</p>
 *
 * <p>This strategy is useful for static reward models where every bet has the same probability of winning,
 * regardless of jackpot pool size or history.</p>
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

        return randomNumberGenerator.getAsDouble() < chance.fractionalValue().doubleValue();
    }
}
