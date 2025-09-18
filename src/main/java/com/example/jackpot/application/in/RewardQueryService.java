package com.example.jackpot.application.in;

import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;

import java.util.Optional;

public interface RewardQueryService {

    Optional<JackpotReward> findByBetId(BetId id);
}
