package com.example.jackpot.application.out;

import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;

import java.util.Optional;

public interface JackpotRewardRepository {
    Optional<JackpotReward> findByBetId(BetId betId);

    void save(JackpotReward reward);
}
