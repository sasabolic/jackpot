package com.example.jackpot.adapter.out.persistence.jpa.repository;

import com.example.jackpot.adapter.out.persistence.jpa.entity.BetEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotContributionEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
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
class ContributionJpaRepositoryTest {

    @Autowired
    private ContributionJpaRepository repository;

    @Autowired
    private TestEntityManager em;

    private JackpotContributionEntity contribution;

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

        contribution = new JackpotContributionEntity(
                bet.getId(),
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR"),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR"),
                Instant.now()
        );
        em.persist(contribution);
        em.flush();
        em.clear();
    }

    @Test
    void whenExistsByBetId_thenResultFound() {
        boolean result = repository.existsByBetId(bet.getId());

        assertThat(result).isTrue();
    }

    @Test
    void givenNonExistingBetContribution_whenExistsByBetId_thenResultFound() {
        boolean result = repository.existsByBetId(UUID.randomUUID());

        assertThat(result).isFalse();
    }

    @Test
    void givenContributionForANewBet_whenSave_thenSuccess() {
        BetEntity newBet = new BetEntity(
                UUID.randomUUID(),
                userId,
                jackpot.getId(),
                new MoneyEmbeddable(new BigDecimal("1.50"), "EUR")
        );
        em.persist(newBet);
        em.flush();

        var newContribution = new JackpotContributionEntity(
                newBet.getId(),
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(new BigDecimal("2.00"), "EUR"),
                new MoneyEmbeddable(new BigDecimal("1.00"), "EUR"),
                new MoneyEmbeddable(new BigDecimal("10.00"), "EUR"),
                Instant.now()
        );

        repository.save(newContribution);
        em.flush();
        em.clear();

        Optional<JackpotContributionEntity> result = repository.findById(newBet.getId());
        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.getBetId()).isEqualTo(newBet.getId());
                    assertThat(r.getUserId()).isEqualTo(userId);
                    assertThat(r.getJackpotId()).isEqualTo(jackpot.getId());
                    assertThat(r.getStake().getAmount()).isEqualByComparingTo(new BigDecimal("2.00"));
                    assertThat(r.getStake().getCurrency()).isEqualTo("EUR");
                    assertThat(r.getContribution().getAmount()).isEqualByComparingTo(new BigDecimal("1.00"));
                    assertThat(r.getContribution().getCurrency()).isEqualTo("EUR");
                    assertThat(r.getCurrentJackpot().getAmount()).isEqualByComparingTo(new BigDecimal("10.00"));
                    assertThat(r.getCurrentJackpot().getCurrency()).isEqualTo("EUR");
                    assertThat(r.getCreatedAt()).isNotNull();
                    assertThat(r.getId()).isEqualTo(newBet.getId());
                });
    }

    @Test
    void givenDuplicateContribution_whenSave_thenThrowException() {
        JackpotContributionEntity duplicateContribution = new JackpotContributionEntity(
                bet.getId(),
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(new BigDecimal("3.00"), "EUR"),
                new MoneyEmbeddable(new BigDecimal("1.50"), "EUR"),
                new MoneyEmbeddable(new BigDecimal("11.00"), "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(duplicateContribution);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenContributionWithNonExistingBet_whenSave_thenThrowException() {
        UUID nonExistingBetId = UUID.randomUUID();

        JackpotContributionEntity newContribution = new JackpotContributionEntity(
                nonExistingBetId,
                userId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR"),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(newContribution);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenContributionWithNonExistingJackpot_whenSave_thenThrowException() {
        UUID nonExistingJackpotId = UUID.randomUUID();

        JackpotContributionEntity newContribution = new JackpotContributionEntity(
                bet.getId(),
                userId,
                nonExistingJackpotId,
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR"),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(newContribution);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void givenContributionWithNonExistingUser_whenSave_thenThrowException() {
        UUID nonExistingUserId = UUID.randomUUID();

        JackpotContributionEntity newContribution = new JackpotContributionEntity(
                bet.getId(),
                nonExistingUserId,
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.TWO, "EUR"),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR"),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR"),
                Instant.now()
        );

        assertThatThrownBy(() -> {
            repository.save(newContribution);
            em.flush();
            em.clear();
        }).isInstanceOf(ConstraintViolationException.class);
    }
}