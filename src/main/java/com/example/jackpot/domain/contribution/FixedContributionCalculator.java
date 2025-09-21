package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static com.example.jackpot.domain.model.vo.Percentage.HUNDRED;
import static com.example.jackpot.domain.model.vo.Percentage.ZERO;
import static java.util.Objects.requireNonNull;

/**
 * Computes a <b>fixed contribution</b> to the jackpot as a constant percentage of the bet amount.
 *
 * <p><b>Formula:</b></p>
 * <pre>
 * contribution = betAmount × rate
 * </pre>
 *
 * <p>Where {@code rate} is a fixed {@link Percentage} configured at construction time.</p>
 *
 * <p>This calculator applies the same contribution rate regardless of jackpot pool size or bet history.
 * It is simple, predictable, and useful for flat-rate jackpot models.</p>
 *
 * <p>All monetary values are represented using {@link Money}, and percentage values using {@link Percentage}.</p>
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
