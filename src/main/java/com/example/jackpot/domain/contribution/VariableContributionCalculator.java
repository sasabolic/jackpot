package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.DecayFactor;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;

import java.math.BigDecimal;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.HUNDRED;
import static com.example.jackpot.domain.model.vo.Percentage.ZERO;
import static java.util.Objects.requireNonNull;

/**
 * Computes a <b>variable contribution</b> (as a percentage of the bet amount) that
 * starts high and linearly decays as the jackpot pool grows, but never below {@code minimumRate}.
 */
public class VariableContributionCalculator implements ContributionCalculator {
    private final Percentage startingRate;
    private final Percentage minimumRate;
    private final DecayFactor decayFactor;

    public VariableContributionCalculator(Percentage startingRate, Percentage minimumRate, DecayFactor decayFactor) {
        requireNonNull(startingRate, "startingRate must not be null");
        requireNonNull(minimumRate, "minimumRate must not be null");
        requireNonNull(decayFactor, "decayFactor must not be null");

        isTrue(startingRate.isGreaterThan(ZERO) && startingRate.isLessThan(HUNDRED), "startingRate must be greater than 0.00 and less than 100.00");
        isTrue(minimumRate.isGreaterThan(ZERO) && minimumRate.isLessThan(HUNDRED), "minimumRate must be greater than 0.00 and less than 100.00");
        isTrue(startingRate.isGreaterThan(minimumRate), "startingRate must be greater than minimumRate");

        this.startingRate = startingRate;
        this.minimumRate = minimumRate;
        this.decayFactor = decayFactor;
    }

    @Override
    public Money calculate(ContributionContext ctx) {
        requireNonNull(ctx, "ctx must not be null");

        BigDecimal delta = ctx.currentPool().minus(ctx.initialPool()).amount();

        Percentage rate = Percentage.of(startingRate.value()
                .subtract(decayFactor.value().multiply(delta))
                .max(minimumRate.value()));

        return ctx.betAmount().times(rate.fractionalValue());
    }
}
