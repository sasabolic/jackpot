package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.HUNDRED;
import static com.example.jackpot.domain.model.vo.Percentage.ZERO;
import static java.util.Objects.requireNonNull;

/**
 * Computes a <b>fixed contribution</b> to the jackpot as a constant percentage of the bet amount.
 */
public class FixedContributionCalculator implements ContributionCalculator {
    private final Percentage rate;

    public FixedContributionCalculator(Percentage rate) {
        requireNonNull(rate, "rate must not be null");
        isTrue(rate.isGreaterThan(ZERO) && rate.isLessThan(HUNDRED), "rate must be between 0.00 and 100.00");

        this.rate = rate;
    }

    @Override
    public Money calculate(ContributionContext ctx) {
        requireNonNull(ctx, "ctx must not be null");

        return ctx.betAmount().times(rate.fractionalValue());
    }
}
