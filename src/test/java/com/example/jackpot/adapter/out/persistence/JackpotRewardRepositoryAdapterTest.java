package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotRewardEntity;
import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import com.example.jackpot.adapter.out.persistence.jpa.repository.RewardJpaRepository;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.CycleNumber;
import com.example.jackpot.domain.model.vo.JackpotCycle;
import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class JackpotRewardRepositoryAdapterTest {

    @Mock
    private RewardJpaRepository repository;

    private JackpotRewardRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JackpotRewardRepositoryAdapter(repository);
    }

    @Test
    void whenSave_thenReturnCorrectResult() {
        JackpotReward reward = new JackpotReward(
                BetId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                JackpotCycle.of(
                        JackpotId.of(UUID.randomUUID()),
                        CycleNumber.of(1)
                ),
                Money.of("230.54", "EUR")
        );

        adapter.save(reward);

        ArgumentCaptor<JackpotRewardEntity> captor = ArgumentCaptor.forClass(JackpotRewardEntity.class);
        then(repository).should().save(captor.capture());
        then(repository).shouldHaveNoMoreInteractions();

        assertThat(captor.getValue())
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(reward.betId().value());
                    assertThat(e.getUserId()).isEqualTo(reward.userId().value());
                    assertThat(e.getJackpotId()).isEqualTo(reward.jackpotId().value());
                    assertThat(e.getReward().getAmount()).isEqualByComparingTo(reward.rewardAmount().amount());
                    assertThat(e.getReward().getCurrency()).isEqualTo(reward.rewardAmount().currency().toString());
                    assertThat(e.getCreatedAt()).isEqualTo(reward.createdAt());
                });
    }

    @Test
    void whenFindByBetId_thenReturnCorrectResult() {
        UUID betId = UUID.randomUUID();
        JackpotRewardEntity entity = new JackpotRewardEntity(
                betId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                1,
                new MoneyEmbeddable(BigDecimal.ONE, "EUR"),
                Instant.now()
        );

        given(repository.findByBetId(betId)).willReturn(Optional.of(entity));

        Optional<JackpotReward> result = adapter.findByBetId(BetId.of(betId));

        assertThat(result)
                .isNotEmpty()
                .hasValueSatisfying(r -> {
                    assertThat(r.betId().value()).isEqualTo(entity.getId());
                    assertThat(r.userId().value()).isEqualTo(entity.getUserId());
                    assertThat(r.jackpotId().value()).isEqualTo(entity.getJackpotId());
                    assertThat(r.rewardAmount().amount()).isEqualByComparingTo(entity.getReward().getAmount());
                    assertThat(r.rewardAmount().currency()).hasToString(entity.getReward().getCurrency());
                    assertThat(r.createdAt()).isEqualTo(entity.getCreatedAt());
                });
    }
}