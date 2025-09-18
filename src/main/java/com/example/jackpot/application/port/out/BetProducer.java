package com.example.jackpot.application.port.out;

import com.example.jackpot.domain.model.Bet;

/**
 * Outbound port for publishing {@link Bet} events
 * to a message broker (e.g., Kafka).
 */
public interface BetProducer {

    void publish(Bet bet);
}