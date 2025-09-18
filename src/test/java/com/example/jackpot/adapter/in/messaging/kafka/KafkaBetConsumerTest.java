package com.example.jackpot.adapter.in.messaging.kafka;

import com.example.jackpot.adapter.out.messaging.kafka.BetMessage;
import com.example.jackpot.application.port.in.BetProcessingService;
import com.example.jackpot.domain.model.Bet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.timeout;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "jackpot-bets" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaBetConsumerTest {

    @Autowired
    private KafkaTemplate<String, BetMessage> kafkaTemplate;

    @MockitoBean
    private BetProcessingService betProcessingService;

    @Test
    void givenBetMessage_whenSend_thenConsumerIsTriggeringProcessingOfBet() {
        UUID jackpotId = UUID.randomUUID();
        BetMessage message = new BetMessage(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                jackpotId.toString(),
                "20.00",
                "EUR"
        );

        kafkaTemplate.send("jackpot-bets", jackpotId.toString(), message);

        ArgumentCaptor<Bet> captor = ArgumentCaptor.forClass(Bet.class);
        then(betProcessingService).should(timeout(3000)).process(captor.capture());

        assertThat(captor.getValue())
                .isNotNull()
                .satisfies(b -> {
                    assertThat(b.betId().value()).isEqualTo(UUID.fromString(message.betId()));
                    assertThat(b.userId().value()).isEqualTo(UUID.fromString(message.userId()));
                    assertThat(b.jackpotId().value()).isEqualTo(UUID.fromString(message.jackpotId()));
                    assertThat(b.betAmount().amount()).isEqualByComparingTo(new BigDecimal(message.amount()));
                    assertThat(b.betAmount().currency()).hasToString(message.currency());
                });
    }
}