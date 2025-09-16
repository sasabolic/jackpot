package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.mapper.JackpotContributionMapper;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.ContributionJpaRepository;
import com.example.jackpot.application.out.JackpotContributionRepository;
import com.example.jackpot.domain.model.JackpotContribution;
import com.example.jackpot.domain.model.id.BetId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JackpotContributionAdapter implements JackpotContributionRepository {

    private final ContributionJpaRepository repository;

    @Override
    public boolean existsByBetId(BetId betId) {
        return repository.existsByBetId(betId.value());
    }

    @Override
    public void save(JackpotContribution contribution) {
        repository.save(JackpotContributionMapper.toEntity(contribution));
    }
}
