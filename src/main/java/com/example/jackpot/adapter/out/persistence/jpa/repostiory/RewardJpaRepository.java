package com.example.jackpot.adapter.out.persistence.jpa.repostiory;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RewardJpaRepository extends JpaRepository<JackpotRewardEntity, UUID> {

    Optional<JackpotRewardEntity> findByBetId(UUID value);
}