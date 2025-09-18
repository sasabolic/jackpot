package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.DoubleSupplier;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.HUNDRED;
import static java.util.Objects.requireNonNull;

/**
 * Evaluates a jackpot win with a <b>variable (linearly increasing) chance</b> expressed as a {@link Percentage}.
 * <p>
 * The chance starts at {@code minPercent} and stays with this value until {@code minPool} is reached,
 * after that it grows linearly towards {@code maxPercent} as the pool approaches a hard cap {@code maxPool}.
 * Once the current pool is at or above the cap, the outcome is a guaranteed win.
 */
@Slf4j
public class VariableChanceRewardEvaluator implements RewardEvaluator {
    private static final int SCALE = 10;
    private static final RoundingMode ROUNDING = RoundingMode.DOWN;

    private final Percentage minPercent;
    private final Percentage maxPercent;
    private final Money minPool;
    private final Money maxPool;
    private final DoubleSupplier randomNumberGenerator;

    public VariableChanceRewardEvaluator(Percentage minPercent, Percentage maxPercent, Money minPool, Money maxPool) {
        this(minPercent, maxPercent, minPool, maxPool, new java.security.SecureRandom()::nextDouble);
    }

    public VariableChanceRewardEvaluator(Percentage minPercent, Percentage maxPercent, Money minPool, Money maxPool, DoubleSupplier randomNumberGenerator) {
        requireNonNull(minPercent, "minPercent must not be null");
        requireNonNull(maxPercent, "maxPercent must not be null");
        requireNonNull(minPool, "minPool must not be null");
        requireNonNull(maxPool, "maxPool must not be null");
        requireNonNull(randomNumberGenerator, "randomNumberGenerator must not be null");

        isTrue(minPercent.isLessThan(HUNDRED), "minPercent must be < 100%");
        isTrue(minPercent.isLessThan(maxPercent), "minPercent must be < maxPercent");
        isTrue(minPool.isPositive(), "minPool must be > 0");
        isTrue(maxPool.isPositive(), "maxPool must be > 0");
        isTrue(minPool.hasSameCurrencyAs(maxPool), "minPool and maxPool must share the same currency");
        isTrue(maxPool.isGreaterThanOrEqual(minPool), "maxPool must be >= minPool");

        this.minPercent = minPercent;
        this.maxPercent = maxPercent;
        this.minPool = minPool;
        this.maxPool = maxPool;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Override
    public boolean evaluate(RewardContext ctx) {
        requireNonNull(ctx, "ctx must not be null");

        isTrue(ctx.currentPool().hasSameCurrencyAs(minPool, maxPool), "Jackpot currentPool, minPool and maxPool must have same currency");

        if (ctx.currentPool().isGreaterThanOrEqual(maxPool)) {
            return true;
        }

        if (ctx.currentPool().isLessThanOrEqual(minPool)) {
            log.info("Chance={}", minPercent);
            return randomNumberGenerator.getAsDouble() < minPercent.fractionalValue().doubleValue();
        }

        BigDecimal poolRange = maxPool.minus(minPool).amount();
        BigDecimal currentRange = ctx.currentPool().minus(minPool).amount();
        Percentage chanceRange = maxPercent.minus(minPercent);

        BigDecimal ratio = currentRange.divide(poolRange, SCALE, ROUNDING);

        Percentage chance = minPercent.plus(chanceRange.times(ratio));

        log.info("Chance={}", chance);

        return randomNumberGenerator.getAsDouble() < chance.fractionalValue().doubleValue();
    }
}
