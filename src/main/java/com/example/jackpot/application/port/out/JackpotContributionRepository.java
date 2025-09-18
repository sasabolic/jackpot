package com.example.jackpot.application.port.out;

import com.example.jackpot.domain.model.JackpotContribution;
import com.example.jackpot.domain.model.id.BetId;

/**
 * Outbound repository port for retrieving and persisting {@link JackpotContribution}
 */
public interface JackpotContributionRepository {
    boolean existsByBetId(BetId betId);

    void save(JackpotContribution contribution);
}
