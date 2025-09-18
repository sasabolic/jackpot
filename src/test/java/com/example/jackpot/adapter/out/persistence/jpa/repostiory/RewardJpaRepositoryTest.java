package com.example.jackpot.adapter.out.persistence.jpa.repostiory;

import com.example.jackpot.adapter.out.persistence.jpa.entity.BetEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotRewardEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
class RewardJpaRepositoryTest {

    @Autowired
    private RewardJpaRepository repository;

    @Autowired
    private TestEntityManager em;

    private JackpotRewardEntity reward;

    private UUID userId;

    private BetEntity bet;

    private JackpotEntity jackpot;

    @BeforeEach
    void setUp() {
        jackpot = new JackpotEntity(UUID.randomUUID(),
                1,
                new MoneyEmbeddable(BigDecimal.ZERO, "EUR"),
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                "{\"type\":\"FIXED\",\"schemaVersion\":1,\"config\":{\"rate\":\"5.00\"}}",
                "{\"type\":\"FIXED_CHANCE\",\"schemaVersion\":1,\"config\":{\"chancePercent\":\"2.50\"}}");
        em.persist(jackpot);

        userId = UUID.randomUUID();
        bet = new BetEntity(
                UUID.randomUUID(),
                userId,
                jackpot.getId(),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR")
        );
        em.persist(bet);

        reward = new JackpotRewardEntity(
                bet.getId(),
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                Instant.now()
        );
        em.persist(reward);
        em.flush();
        em.clear();
    }

    @Test
    void whenFindByBetId_thenResultFound() {
        Optional<JackpotRewardEntity> result = repository.findByBetId(bet.getId());

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.getBetId()).isEqualTo(bet.getId());
                    assertThat(r.getUserId()).isEqualTo(userId);
                    assertThat(r.getJackpotId()).isEqualTo(jackpot.getId());
                    assertThat(r.getReward().getAmount()).isEqualByComparingTo(reward.getReward().getAmount());
                    assertThat(r.getReward().getCurrency()).isEqualTo(reward.getReward().getCurrency());
                    assertThat(r.getCreatedAt()).isNotNull();
                    assertThat(r.getId()).isEqualTo(bet.getId());
                });
    }

    @Test
    void givenNonExistingReward_whenFindByBetId_thenResultNotFound() {
        Optional<JackpotRewardEntity> result = repository.findByBetId(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void givenRewardForANewBet_whenSave_thenSuccess() {
        BetEntity newBet = new BetEntity(
                UUID.randomUUID(),
                userId,
                jackpot.getId(),
                new MoneyEmbeddable(new BigDecimal("1.50"), "EUR")
        );
        em.persist(newBet);
        em.flush();

        JackpotRewardEntity newReward = new JackpotRewardEntity(
                newBet.getId(),
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(new BigDecimal("2.00"), "EUR"),
                Instant.now()
        );

        repository.save(newReward);
        em.flush();
        em.clear();

        Optional<JackpotRewardEntity> result = repository.findById(newBet.getId());
        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.getBetId()).isEqualTo(newBet.getId());
                    assertThat(r.getUserId()).isEqualTo(userId);
                    assertThat(r.getJackpotId()).isEqualTo(jackpot.getId());
                    assertThat(r.getReward().getAmount()).isEqualByComparingTo(newReward.getReward().getAmount());
                    assertThat(r.getReward().getCurrency()).isEqualTo(newReward.getReward().getCurrency());
                    assertThat(r.getCreatedAt()).isNotNull();
                    assertThat(r.getId()).isEqualTo(newBet.getId());
                });
    }

    @Test
    void givenDuplicateReward_whenSave_thenThrowException() {
        JackpotRewardEntity duplicateReward = new JackpotRewardEntity(
                bet.getId(),
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(new BigDecimal("3.00"), "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(duplicateReward);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenRewardWithNonExistingBet_whenSave_thenThrowException() {
        UUID nonExistingBetId = UUID.randomUUID();

        JackpotRewardEntity newReward = new JackpotRewardEntity(
                nonExistingBetId,
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(newReward);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenRewardWithNonExistingJackpot_whenSave_thenThrowException() {
        UUID nonExistingJackpotId = UUID.randomUUID();

        JackpotRewardEntity newReward = new JackpotRewardEntity(
                bet.getId(),
                userId,
                nonExistingJackpotId,
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(newReward);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenRewardWithNonExistingUser_whenSave_thenThrowException() {
        UUID nonExistingUserId = UUID.randomUUID();

        JackpotRewardEntity newReward = new JackpotRewardEntity(
                bet.getId(),
                nonExistingUserId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(newReward);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}