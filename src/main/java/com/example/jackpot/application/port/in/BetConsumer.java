package com.example.jackpot.application.port.in;

import com.example.jackpot.adapter.out.messaging.kafka.BetMessage;

/**
 * Inbound port for receiving bet messages from a message broker (e.g., Kafka).
 */
public interface BetConsumer {

    void onMessage(BetMessage msg);
}
