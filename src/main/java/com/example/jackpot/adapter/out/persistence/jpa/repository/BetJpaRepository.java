package com.example.jackpot.adapter.out.persistence.jpa.repository;

import com.example.jackpot.adapter.out.persistence.jpa.entity.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BetJpaRepository extends JpaRepository<BetEntity, UUID> {
}