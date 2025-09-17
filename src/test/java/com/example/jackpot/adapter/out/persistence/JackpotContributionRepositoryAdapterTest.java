package com.example.jackpot.adapter.out.persistence;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotContributionEntity;
import com.example.jackpot.adapter.out.persistence.jpa.repostiory.ContributionJpaRepository;
import com.example.jackpot.domain.model.JackpotContribution;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class JackpotContributionRepositoryAdapterTest {

    @Mock
    private ContributionJpaRepository repository;

    private JackpotContributionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JackpotContributionRepositoryAdapter(repository);
    }

    @Test
    void whenSave_thenReturnCorrectResult() {
        JackpotContribution contribution = new JackpotContribution(
                BetId.of(UUID.randomUUID()),
                UserId.of(UUID.randomUUID()),
                JackpotId.of(UUID.randomUUID()),
                Money.of("100.00", "EUR"),
                Money.of("20.54", "EUR"),
                Money.of("230.57", "EUR")
        );

        adapter.save(contribution);

        ArgumentCaptor<JackpotContributionEntity> captor = ArgumentCaptor.forClass(JackpotContributionEntity.class);
        then(repository).should().save(captor.capture());
        then(repository).shouldHaveNoMoreInteractions();

        assertThat(captor.getValue())
                .isNotNull()
                .satisfies(e -> {
                    assertThat(e.getId()).isEqualTo(contribution.betId().value());
                    assertThat(e.getUserId()).isEqualTo(contribution.userId().value());
                    assertThat(e.getJackpotId()).isEqualTo(contribution.jackpotId().value());
                    assertThat(e.getStake().getAmount()).isEqualByComparingTo(contribution.stakeAmount().amount());
                    assertThat(e.getStake().getCurrency()).isEqualTo(contribution.stakeAmount().currency().toString());
                    assertThat(e.getContribution().getAmount()).isEqualByComparingTo(contribution.contributionAmount().amount());
                    assertThat(e.getContribution().getCurrency()).isEqualTo(contribution.contributionAmount().currency().toString());
                    assertThat(e.getCurrentJackpot().getAmount()).isEqualByComparingTo(contribution.currentJackpotAmount().amount());
                    assertThat(e.getCurrentJackpot().getCurrency()).isEqualTo(contribution.currentJackpotAmount().currency().toString());
                    assertThat(e.getCreatedAt()).isEqualTo(contribution.createdAt());
                });
    }

    @Test
    void whenExistsByBetId_thenReturnCorrectResult() {
        boolean exits = true;
        UUID betId = UUID.randomUUID();

        given(repository.existsByBetId(betId)).willReturn(exits);

        boolean result = adapter.existsByBetId(BetId.of(betId));

        assertThat(result).isTrue();
    }
}