package com.example.jackpot.adapter.out.persistence.jpa.repostiory;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotContributionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContributionJpaRepository extends JpaRepository<JackpotContributionEntity, UUID> {

    boolean existsByBetId(UUID betId);
}