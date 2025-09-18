package com.example.jackpot.adapter.out.persistence.jpa.repostiory;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class JackpotJpaRepositoryTest {

    @Autowired
    private JackpotJpaRepository repository;

    @Autowired
    private TestEntityManager em;

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
        em.flush();
        em.clear();
    }

    @Test
    void whenFindById_thenResultFound() {
        Optional<JackpotEntity> result = repository.findById(jackpot.getId());

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.getId()).isEqualTo(jackpot.getId());
                    assertThat(r.getInitial().getAmount()).isEqualByComparingTo(jackpot.getInitial().getAmount());
                    assertThat(r.getInitial().getCurrency()).isEqualTo(jackpot.getInitial().getCurrency());
                    assertThat(r.getCurrent().getAmount()).isEqualByComparingTo(jackpot.getCurrent().getAmount());
                    assertThat(r.getCurrent().getCurrency()).isEqualTo(jackpot.getCurrent().getCurrency());
                    assertThat(r.getContributionConfigJson()).isEqualTo(jackpot.getContributionConfigJson());
                    assertThat(r.getRewardConfigJson()).isEqualTo(jackpot.getRewardConfigJson());
                });

    }

    @Test
    void givenNonExistingId_whenFindById_thenNoResult() {
        Optional<JackpotEntity> result = repository.findById(UUID.randomUUID());

        assertThat(result).isEmpty();
    }

    @Test
    void givenJackpotUpdated_whenSaved_thenExpectedFieldsNotChanged() {
        JackpotEntity updatedJackpot = new JackpotEntity(
                jackpot.getId(),
                1,
                new MoneyEmbeddable(BigDecimal.ONE, "EUR"),
                new MoneyEmbeddable(BigDecimal.TEN, "EUR"),
                "{\"type\":\"FIXED\",\"schemaVersion\":1,\"config\":{\"rate\":\"6.00\"}}",
                "{\"type\":\"FIXED_CHANCE\",\"schemaVersion\":1,\"config\":{\"chancePercent\":\"3.50\"}}");
        updatedJackpot.setVersion(jackpot.getVersion());

        repository.save(updatedJackpot);
        em.flush();
        em.clear();

        Optional<JackpotEntity> result = repository.findById(updatedJackpot.getId());

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.getId()).isEqualTo(jackpot.getId());
                    assertThat(r.getInitial().getAmount()).isNotEqualByComparingTo(jackpot.getInitial().getAmount());
                    assertThat(r.getInitial().getCurrency()).isEqualTo(jackpot.getInitial().getCurrency());
                    assertThat(r.getCurrent().getAmount()).isNotEqualByComparingTo(jackpot.getCurrent().getAmount());
                    assertThat(r.getCurrent().getCurrency()).isEqualTo(jackpot.getCurrent().getCurrency());
                    assertThat(r.getContributionConfigJson()).isEqualTo(jackpot.getContributionConfigJson());
                    assertThat(r.getRewardConfigJson()).isEqualTo(jackpot.getRewardConfigJson());
                });
    }
}