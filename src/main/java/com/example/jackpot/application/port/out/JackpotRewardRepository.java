package com.example.jackpot.application.port.out;

import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;

import java.util.Optional;

/**
 * Outbound repository port for retrieving and persisting {@link JackpotReward}
 */
public interface JackpotRewardRepository {
    Optional<JackpotReward> findByBetId(BetId betId);

    void save(JackpotReward reward);
}
