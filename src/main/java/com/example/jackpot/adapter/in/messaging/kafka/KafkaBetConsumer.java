package com.example.jackpot.adapter.in.messaging.kafka;

import com.example.jackpot.adapter.out.messaging.kafka.BetMessage;
import com.example.jackpot.application.in.BetProcessingService;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBetConsumer {

    private final BetProcessingService betProcessingService;

    @KafkaListener(
            topics = "${kafka.topic.bets:jackpot-bets}",
            groupId = "${spring.kafka.consumer.group-id:jackpot-bet-processing-group}"
    )
    public void onMessage(@Payload BetMessage e) {
        log.info("Consumed BetMessage: {}", e);

        try {
            Bet bet = e.toDomain();

            betProcessingService.process(bet);
        } catch (Exception ex) {
            log.error("Failed to process BetMessage (betId={}, jackpotId={}): {}", e.betId(), e.jackpotId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
