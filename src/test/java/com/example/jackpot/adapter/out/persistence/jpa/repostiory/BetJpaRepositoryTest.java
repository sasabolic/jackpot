package com.example.jackpot.adapter.out.persistence.jpa.repostiory;

import com.example.jackpot.adapter.out.persistence.jpa.entity.BetEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
class BetJpaRepositoryTest {

    @Autowired
    private BetJpaRepository repository;

    @Autowired
    private TestEntityManager em;

    private JackpotEntity jackpot;

    private BetEntity bet;

    @BeforeEach
    void setUp() {
        jackpot = new JackpotEntity(UUID.randomUUID(),
                1,
                new MoneyEmbeddable(BigDecimal.ZERO, "EUR"),
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                "{\"type\":\"FIXED\",\"schemaVersion\":1,\"config\":{\"rate\":\"5.00\"}}",
                "{\"type\":\"FIXED_CHANCE\",\"schemaVersion\":1,\"config\":{\"chancePercent\":\"2.50\"}}");
        em.persist(jackpot);

        bet = new BetEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                jackpot.getId(),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR")
        );
        em.persist(bet);
        em.flush();
        em.clear();
    }

    @Test
    void whenExistsById_thenTrue() {
        boolean result = repository.existsById(bet.getId());

        assertThat(result).isTrue();
    }

    @Test
    void givenBetOfExistingJackpot_whenSave_thenSuccess() {
        BetEntity newBet = new BetEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                jackpot.getId(),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR")
        );

        repository.save(newBet);
        em.flush();
        em.clear();

        Optional<BetEntity> result = repository.findById(newBet.getId());

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.getId()).isEqualTo(newBet.getId());
                    assertThat(r.getJackpotId()).isEqualTo(newBet.getJackpotId());
                    assertThat(r.getUserId()).isEqualTo(newBet.getUserId());
                    assertThat(r.getBet().getAmount()).isEqualByComparingTo(newBet.getBet().getAmount());
                    assertThat(r.getBet().getCurrency()).isEqualTo(newBet.getBet().getCurrency());
                });
    }

    @Test
    void ivenDuplicateBet_whenSave_thenThrowException() {
        BetEntity duplicateBet = new BetEntity(
                bet.getId(),
                UUID.randomUUID(),
                jackpot.getId(),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR")
        );

        assertThatThrownBy(() -> {
            repository.save(duplicateBet);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenBetWithNonExistingJackpot_whenSave_thenThrowException() {
        BetEntity newBet = new BetEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR")
        );

        assertThatThrownBy(() -> {
            repository.save(newBet);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}