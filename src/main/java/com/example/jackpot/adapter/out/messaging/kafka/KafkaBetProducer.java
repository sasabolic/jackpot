package com.example.jackpot.adapter.out.messaging.kafka;

import com.example.jackpot.application.port.out.BetProducer;
import com.example.jackpot.domain.model.Bet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBetProducer implements BetProducer {

    private final KafkaTemplate<String, BetMessage> kafka;

    @Value("${kafka.topic.bets:jackpot-bets}")
    private String topic;

    @Override
    public void publish(Bet bet) {
        BetMessage msg = BetMessage.from(bet);
        String key = msg.jackpotId();

        log.info("Publishing message={} to topic {}", msg, topic);

        kafka.send(topic, key, msg).whenComplete((r, e) -> {
            if (e != null) {
                log.error("Publish failed betId={} jackpotId={}: {}", msg.betId(), msg.jackpotId(), e.toString());
            } else {
                var md = r.getRecordMetadata();
                log.info("Published BetMessage={} to {}-{}@{} key={} betId={}", msg, md.topic(), md.partition(), md.offset(), key, msg.betId());
            }
        });
    }
}
