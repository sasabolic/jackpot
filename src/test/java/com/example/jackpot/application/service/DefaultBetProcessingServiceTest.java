package com.example.jackpot.application.service;

import com.example.jackpot.application.port.in.BetProcessingService;
import com.example.jackpot.application.port.out.JackpotContributionRepository;
import com.example.jackpot.application.port.out.JackpotRepository;
import com.example.jackpot.application.port.out.JackpotRewardRepository;
import com.example.jackpot.domain.exception.JackpotNotFoundException;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.JackpotContribution;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DefaultBetProcessingServiceTest {

    @Mock
    private JackpotRepository jackpotRepository;

    @Mock
    private JackpotContributionRepository contributionRepository;

    @Mock
    private JackpotRewardRepository rewardRepository;

    private BetProcessingService service;

    @BeforeEach
    void setUp() {
        service = new DefaultBetProcessingService(jackpotRepository, contributionRepository, rewardRepository);
    }

    @Test
    void givenNullBet_whenProcess_thenThrowException() {
        assertThatThrownBy(() -> service.process(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("bet must not be null");
    }

    @Test
    void givenContributionAlreadyExists_whenProcess_thenDoNothing() {
        BetId betId = BetId.of(UUID.randomUUID());

        Bet bet = new Bet(
                betId,
                UserId.of(UUID.randomUUID()),
                JackpotId.of(UUID.randomUUID()),
                Money.of("105.00", "EUR")
        );

        given(contributionRepository.existsByBetId(betId)).willReturn(true);

        service.process(bet);

        then(contributionRepository).shouldHaveNoMoreInteractions();
        then(jackpotRepository).shouldHaveNoInteractions();
        then(rewardRepository).shouldHaveNoInteractions();
    }

    @Test
    void givenJackpotDoesNotExist_whenProcess_thenThrowException() {
        BetId betId = BetId.of(UUID.randomUUID());
        JackpotId jackpotId = JackpotId.of(UUID.randomUUID());

        Bet bet = new Bet(
                betId,
                UserId.of(UUID.randomUUID()),
                jackpotId,
                Money.of("105.00", "EUR")
        );

        given(contributionRepository.existsByBetId(betId)).willReturn(false);
        given(jackpotRepository.findById(jackpotId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.process(bet))
                .isInstanceOf(JackpotNotFoundException.class)
                .hasMessageContaining("Jackpot not found: %s".formatted(jackpotId.value()));

        then(jackpotRepository).shouldHaveNoMoreInteractions();
        then(contributionRepository).shouldHaveNoMoreInteractions();
        then(rewardRepository).shouldHaveNoInteractions();
    }

    @Test
    void whenProcess_thenContributedAndRewardEvaluated() {
        BetId betId = BetId.of(UUID.randomUUID());
        JackpotId jackpotId = JackpotId.of(UUID.randomUUID());

        Bet bet = new Bet(
                betId,
                UserId.of(UUID.randomUUID()),
                jackpotId,
                Money.of("105.00", "EUR")
        );

        Jackpot jackpot = mock(Jackpot.class);
        JackpotContribution contribution = mock(JackpotContribution.class);

        given(contributionRepository.existsByBetId(betId)).willReturn(false);
        given(jackpotRepository.findById(jackpotId)).willReturn(Optional.of(jackpot));
        given(jackpot.contribute(isA(Bet.class))).willReturn(contribution);

        service.process(bet);

        then(contributionRepository).should().save(contribution);
        then(jackpotRepository).should().save(jackpot);
        then(rewardRepository).shouldHaveNoInteractions();
    }

    @Test
    void givenRewarded_whenProcess_thenRewordSavedAndNewCycleStarted() {
        BetId betId = BetId.of(UUID.randomUUID());
        JackpotId jackpotId = JackpotId.of(UUID.randomUUID());

        Bet bet = new Bet(
                betId,
                UserId.of(UUID.randomUUID()),
                jackpotId,
                Money.of("105.00", "EUR")
        );

        Jackpot jackpot = mock(Jackpot.class);
        JackpotContribution contribution = mock(JackpotContribution.class);
        JackpotReward reward = mock(JackpotReward.class);

        given(contributionRepository.existsByBetId(betId)).willReturn(false);
        given(jackpotRepository.findById(jackpotId)).willReturn(Optional.of(jackpot));
        given(jackpot.contribute(isA(Bet.class))).willReturn(contribution);
        given(jackpot.evaluateRewardFor(bet)).willReturn(Optional.of(reward));

        service.process(bet);

        then(contributionRepository).should(times(1)).save(contribution);
        then(jackpotRepository).should(times(1)).save(eq(jackpot));
        then(rewardRepository).should(times(1)).save(eq(reward));
        then(jackpot).should(times(1)).startNextCycle();

        then(contributionRepository).shouldHaveNoMoreInteractions();
        then(jackpotRepository).shouldHaveNoMoreInteractions();
        then(rewardRepository).shouldHaveNoMoreInteractions();
    }
}

