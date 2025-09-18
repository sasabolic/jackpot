package com.example.jackpot.application.out;

import com.example.jackpot.domain.model.Bet;

public interface BetProducer {

    void publish(Bet bet);
}