package com.example.jackpot.adapter.out.messaging.kafka;

import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KafkaBetProducerErrorTest {

    @Mock
    private KafkaTemplate<String, BetMessage> kafka;

    private KafkaBetProducer kafkaBetProducer;

    @BeforeEach
    void setUp() {
        kafkaBetProducer = new KafkaBetProducer(kafka);
    }

    @Test
    void shouldLogErrorWhenKafkaSendFails() {
        LogCaptor logCaptor = LogCaptor.forClass(KafkaBetProducer.class);

        Bet bet = new Bet(
                BetId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                JackpotId.of(UUID.randomUUID()),
                Money.of("10.00", "EUR")
        );

        CompletableFuture<SendResult<String, BetMessage>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka send failed"));

        given(kafka.send(any(), anyString(), any(BetMessage.class))).willReturn(failedFuture);

        kafkaBetProducer.publish(bet);

        assertThat(logCaptor.getErrorLogs()).anyMatch(log -> log.contains("Publish failed betId=%s jackpotId=%s:".formatted(bet.betId(), bet.jackpotId())) && log.contains("Kafka send failed"));
    }
}