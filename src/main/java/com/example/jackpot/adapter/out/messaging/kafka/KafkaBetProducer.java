package com.example.jackpot.adapter.out.messaging.kafka;

import com.example.jackpot.application.port.out.BetProducer;
import com.example.jackpot.domain.model.Bet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBetProducer implements BetProducer {

    private final KafkaTemplate<String, BetMessage> kafka;

    @Value("${jackpot.kafka.topic.bets:jackpot-bets}")
    private String topic;

    @Override
    public void publish(Bet bet) {
        BetMessage betMessage = BetMessage.from(bet);
        String key = betMessage.jackpotId();

        log.info("Publishing message={} to topic {}", betMessage, topic);

        kafka.send(topic, key, betMessage)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Publish failed betId={} jackpotId={}: {}", betMessage.betId(), betMessage.jackpotId(), ex.toString());
                    } else {
                        RecordMetadata md = result.getRecordMetadata();
                        log.info("Published betId={} to {}-{}@{} key={}", betMessage.betId(), md.topic(), md.partition(), md.offset(), key);
                    }
                });
    }
}
