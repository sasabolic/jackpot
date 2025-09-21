package com.example.jackpot.adapter.out.persistence.jpa.repository;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JackpotJpaRepository extends JpaRepository<JackpotEntity, UUID> {
}