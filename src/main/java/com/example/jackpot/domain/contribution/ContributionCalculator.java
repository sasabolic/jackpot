package com.example.jackpot.domain.contribution;

import com.example.jackpot.domain.model.vo.Money;

public interface ContributionCalculator {
    Money calculate(ContributionContext ctx);
}
