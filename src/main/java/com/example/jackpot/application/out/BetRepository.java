package com.example.jackpot.application.out;

import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;

import java.util.Optional;

public interface BetRepository {
    Optional<Bet> findById(BetId id);

    void save(Bet bet);

}
