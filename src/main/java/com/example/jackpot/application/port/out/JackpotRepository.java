package com.example.jackpot.application.port.out;

import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.id.JackpotId;

import java.util.Optional;

/**
 * Repository port for persisting and retrieving {@link Jackpot} aggregates.
 */
public interface JackpotRepository {
    Optional<Jackpot> findById(JackpotId id);

    void save(Jackpot jackpot);
}
