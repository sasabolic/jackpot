package com.example.jackpot.adapter.in.messaging.kafka;

import com.example.jackpot.adapter.out.messaging.kafka.BetMessage;
import com.example.jackpot.application.port.in.BetConsumer;
import com.example.jackpot.application.port.in.BetProcessingService;
import com.example.jackpot.domain.model.Bet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBetConsumer implements BetConsumer {

    private final BetProcessingService betProcessingService;

    @KafkaListener(
            topics = "${kafka.topic.bets:jackpot-bets}",
            groupId = "${spring.kafka.consumer.group-id:jackpot-bet-processing-group}"
    )
    @Override
    public void onMessage(@Payload BetMessage msg) {
        log.info("Consumed BetMessage: {}", msg);

        try {
            Bet bet = msg.toDomain();

            betProcessingService.process(bet);
        } catch (Exception ex) {
            log.error("Failed to process BetMessage (betId={}, jackpotId={}): {}", msg.betId(), msg.jackpotId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
