package com.example.jackpot.application.service;

import com.example.jackpot.application.port.in.RewardQueryService;
import com.example.jackpot.application.port.out.JackpotRewardRepository;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class DefaultRewardQueryService implements RewardQueryService {

    private final JackpotRewardRepository rewardRepository;

    @Override
    public Optional<JackpotReward> findByBetId(BetId betId) {
        requireNonNull(betId, "betId must not be null");

        return rewardRepository.findByBetId(betId);
    }
}