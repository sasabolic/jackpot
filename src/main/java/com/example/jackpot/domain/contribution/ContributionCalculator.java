package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.Money;

/**
 * Strategy interface for computing how much of a bet is contributed to the jackpot pool.
 */
public interface ContributionCalculator {
    Money calculate(ContributionContext ctx);
}
