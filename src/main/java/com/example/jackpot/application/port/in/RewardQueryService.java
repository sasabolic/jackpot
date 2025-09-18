package com.example.jackpot.application.port.in;

import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;

import java.util.Optional;

/**
 * Used for retrieving {@link JackpotReward} data (read-only) by {@link BetId}.
 */
public interface RewardQueryService {

    Optional<JackpotReward> findByBetId(BetId id);
}
