package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.mapper.JackpotRewardMapper;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.RewardJpaRepository;
import com.example.jackpot.application.port.out.JackpotRewardRepository;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JackpotRewardRepositoryAdapter implements JackpotRewardRepository {
    private final RewardJpaRepository repository;

    @Override
    public Optional<JackpotReward> findByBetId(BetId betId) {
        return repository.findByBetId(betId.value()).map(JackpotRewardMapper::toDomain);
    }

    @Override
    public void save(JackpotReward reward) {
        repository.save(JackpotRewardMapper.toEntity(reward));
    }
}
