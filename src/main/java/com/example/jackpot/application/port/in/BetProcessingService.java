package com.example.jackpot.application.port.in;

import com.example.jackpot.domain.model.Bet;

/**
 * Application use case for processing a placed {@link Bet}.
 */
public interface BetProcessingService {

    void process(Bet bet);
}
