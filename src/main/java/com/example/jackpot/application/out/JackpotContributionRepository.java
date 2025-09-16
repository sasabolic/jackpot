package com.example.jackpot.application.out;

import com.example.jackpot.domain.model.JackpotContribution;
import com.example.jackpot.domain.model.id.BetId;

public interface JackpotContributionRepository {
    boolean existsByBetId(BetId betId);

    void save(JackpotContribution contribution);
}
