package com.example.jackpot.application.port.out;

import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;

import java.util.Optional;

/**
 * Repository port for persisting and retrieving {@link Bet} aggregates.
 */
public interface BetRepository {
    Optional<Bet> findById(BetId id);

    boolean existsById(BetId id);

    void save(Bet bet);
}
