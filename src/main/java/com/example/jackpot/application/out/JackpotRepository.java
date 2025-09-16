package com.example.jackpot.application.out;

import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.id.JackpotId;

import java.util.Optional;

public interface JackpotRepository {
    Optional<Jackpot> findById(JackpotId id);

    void save(Jackpot jackpot);
}
