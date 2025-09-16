package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.entity.BetEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.BetJpaRepository;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
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

@ExtendWith(MockitoExtension.class)
class BetRepositoryAdapterTest {

    @Mock
    private BetJpaRepository repository;

    private BetRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BetRepositoryAdapter(repository);
    }

    @Test
    void whenSave_thenReturnCorrectResult() {
        Bet bet = new Bet(
                BetId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                JackpotId.of(UUID.randomUUID()),
                Money.of("100.54", "EUR")
        );

        adapter.save(bet);

        ArgumentCaptor<BetEntity> captor = ArgumentCaptor.forClass(BetEntity.class);
        then(repository).should().save(captor.capture());
        then(repository).shouldHaveNoMoreInteractions();

        assertThat(captor.getValue())
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(bet.betId().value());
                    assertThat(e.getUserId()).isEqualTo(bet.userId().value());
                    assertThat(e.getJackpotId()).isEqualTo(bet.jackpotId().value());
                    assertThat(e.getBet().getAmount()).isEqualByComparingTo(bet.betAmount().amount());
                    assertThat(e.getBet().getCurrency()).isEqualTo(bet.betAmount().currency().toString());
                });
    }

    @Test
    void whenFindById_thenReturnCorrectResult() {
        UUID betId = UUID.randomUUID();
        BetEntity entity = new BetEntity(
                betId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                new MoneyEmbeddable(BigDecimal.ONE, "EUR")
        );

        given(repository.findById(betId)).willReturn(Optional.of(entity));

        Optional<Bet> result = adapter.findById(BetId.of(betId));

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.betId().value()).isEqualTo(entity.getId());
                    assertThat(r.userId().value()).isEqualTo(entity.getUserId());
                    assertThat(r.jackpotId().value()).isEqualTo(entity.getJackpotId());
                    assertThat(r.betAmount().amount()).isEqualByComparingTo(entity.getBet().getAmount());
                    assertThat(r.betAmount().currency()).hasToString(entity.getBet().getCurrency());
                });
    }
}