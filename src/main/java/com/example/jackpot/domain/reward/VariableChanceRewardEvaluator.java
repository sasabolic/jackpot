package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.HUNDRED;
import static java.util.Objects.requireNonNull;

/**
 * Evaluates a jackpot win with a <b>variable (linearly increasing) chance</b> expressed as a {@link Percentage}.
 * <p>
 * The chance starts at {@code startPercent} and grows linearly towards <b>100%</b> as the pool approaches
 * a hard cap {@code rewardPoolLimit}. Once the current pool is at or above the cap, the outcome is a
 * guaranteed win.
 */
public class VariableChanceRewardEvaluator implements RewardEvaluator {
    private static final int SCALE = 10;
    private static final RoundingMode ROUNDING = RoundingMode.DOWN;

    private final Percentage startPercent;
    private final Money rewardPoolLimit;
    private final DoubleSupplier randomNumberGenerator;

    public VariableChanceRewardEvaluator(Percentage startPercent, Money rewardPoolLimit) {
        this(startPercent, rewardPoolLimit, ThreadLocalRandom.current()::nextDouble);
    }

    public VariableChanceRewardEvaluator(Percentage startPercent, Money rewardPoolLimit, DoubleSupplier randomNumberGenerator) {
        requireNonNull(startPercent, "startPercent must not be null");
        requireNonNull(rewardPoolLimit, "rewardPoolLimit must not be null");
        requireNonNull(randomNumberGenerator, "randomNumberGenerator must not be null");

        isTrue(startPercent.isLessThan(HUNDRED), "startPercent must be < 100%");
        isTrue(rewardPoolLimit.isPositive(), "rewardPoolLimit must be > 0");

        this.startPercent = startPercent;
        this.rewardPoolLimit = rewardPoolLimit;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public boolean evaluate(RewardContext ctx) {
        requireNonNull(ctx, "ctx must not be null");

        isTrue(ctx.currentPool().hasSameCurrencyAs(rewardPoolLimit), "Jackpot current pool and reward pool limit must have same currency");

        if (ctx.currentPool().isGreaterThanOrEqual(rewardPoolLimit)) {
            return true;
        }

        BigDecimal ratio = ctx.currentPool().amount().divide(rewardPoolLimit.amount(), SCALE, ROUNDING);

        Percentage chance = startPercent.plus(HUNDRED.minus(startPercent).times(ratio));

        return randomNumberGenerator.getAsDouble() <= chance.fractionalValue().doubleValue();
    }
}
