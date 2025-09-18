package com.example.jackpot.adapter.out.messaging.kafka;

import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "jackpot-bets" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KafkaBetProducerTest {

    @Autowired
    private KafkaBetProducer kafkaBetProducer;

    @Autowired
    private ConsumerFactory<String, BetMessage> consumerFactory;

    private Consumer<String, BetMessage> testConsumer;

    @BeforeEach
    void setUp() {
        testConsumer = consumerFactory.createConsumer("testGroup", "testClient");
        testConsumer.subscribe(List.of("jackpot-bets"));
    }

    @Test
    void whenPublish_thenCorrectMessageSent() {
        Bet bet = new Bet(
                BetId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                JackpotId.of(UUID.randomUUID()),
                Money.of("10.00", "EUR")
        );

        kafkaBetProducer.publish(bet);

        ConsumerRecords<String, BetMessage> result = testConsumer.poll(Duration.ofSeconds(2));

        assertThat(result).isNotEmpty();

        BetMessage message = result.iterator().next().value();

        BetMessage expected = BetMessage.from(bet);


        assertThat(message)
                .isNotNull()
                .satisfies(m -> {
                    assertThat(m.betId()).isEqualTo(expected.betId());
                    assertThat(m.userId()).isEqualTo(expected.userId());
                    assertThat(m.jackpotId()).isEqualTo(expected.jackpotId());
                    assertThat(m.amount()).isEqualTo(expected.amount());
                    assertThat(m.currency()).isEqualTo(expected.currency());
                });
    }

    @AfterEach
    void tearDown() {
        testConsumer.close();
    }
}