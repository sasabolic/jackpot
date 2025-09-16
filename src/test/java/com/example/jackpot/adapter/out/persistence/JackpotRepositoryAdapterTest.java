package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.JackpotJpaRepository;
import com.example.jackpot.domain.contribution.ContributionCalculator;
import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.reward.RewardEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class JackpotRepositoryAdapterTest {

    @Mock
    private JackpotJpaRepository repository;

    private JackpotRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JackpotRepositoryAdapter(repository);
    }

    @Test
    void whenSave_thenReturnCorrectResult() {
        Jackpot jackpot = new Jackpot(
                JackpotId.of(UUID.randomUUID()),
                Money.of("100.00", "EUR"),
                Money.of("230.54", "EUR"),
                mock(ContributionCalculator.class),
                mock(RewardEvaluator.class)
        );

        adapter.save(jackpot);

        ArgumentCaptor<JackpotEntity> captor = ArgumentCaptor.forClass(JackpotEntity.class);
        then(repository).should().save(captor.capture());
        then(repository).shouldHaveNoMoreInteractions();

        assertThat(captor.getValue())
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(jackpot.jackpotId().value());
                    assertThat(e.getInitial().getAmount()).isEqualByComparingTo(jackpot.initialPool().amount());
                    assertThat(e.getInitial().getCurrency()).isEqualTo(jackpot.initialPool().currency().toString());
                    assertThat(e.getCurrent().getAmount()).isEqualByComparingTo(jackpot.currentPool().amount());
                    assertThat(e.getCurrent().getCurrency()).isEqualTo(jackpot.currentPool().currency().toString());
                });
    }

    @Test
    void whenFindById_thenReturnCorrectResult() {
        UUID jackpotId = UUID.randomUUID();
        JackpotEntity entity = new JackpotEntity(
                jackpotId,
                new MoneyEmbeddable(new BigDecimal("100.00"), "EUR"),
                new MoneyEmbeddable(new BigDecimal("230.54"), "EUR"),
                "{\"type\":\"FIXED\",\"schemaVersion\":1,\"config\":{\"rate\":\"6.00\"}}",
                "{\"type\":\"FIXED_CHANCE\",\"schemaVersion\":1,\"config\":{\"chancePercent\":\"3.50\"}}"
        );

        given(repository.findById(jackpotId)).willReturn(Optional.of(entity));

        Optional<Jackpot> result = adapter.findById(JackpotId.of(jackpotId));

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.jackpotId().value()).isEqualTo(entity.getId());
                    assertThat(r.initialPool().amount()).isEqualByComparingTo(entity.getInitial().getAmount());
                    assertThat(r.initialPool().currency()).hasToString(entity.getInitial().getCurrency());
                    assertThat(r.currentPool().amount()).isEqualByComparingTo(entity.getCurrent().getAmount());
                    assertThat(r.currentPool().currency()).hasToString(entity.getCurrent().getCurrency());
                });
    }
}