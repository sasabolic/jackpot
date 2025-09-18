package com.example.jackpot.application.port.in;

import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.Jackpot;

/**
 * Used to record a {@link Bet} placed on a {@link Jackpot} and publish it for downstream reward evaluation.
 */
public interface PlaceBetService {

    void place(Bet bet);
}
