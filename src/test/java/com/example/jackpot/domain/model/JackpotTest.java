package com.example.jackpot.domain.model;

import com.example.jackpot.domain.contribution.ContributionCalculator;
import com.example.jackpot.domain.contribution.ContributionContext;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.CycleNumber;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.reward.RewardContext;
import com.example.jackpot.domain.reward.RewardEvaluator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class JackpotTest {

    @Captor
    ArgumentCaptor<ContributionContext> contributionCtxCaptor;
    @Captor
    ArgumentCaptor<RewardContext> rewardCtxCaptor;

    // ------------------------------------------------------------------
    // Constructor Tests
    // ------------------------------------------------------------------

    @Nested
    class ConstructorTests {

        @Test
        void givenNullJackpotId_whenNewInstance_thenThrowException() {
            JackpotId jackpotId = null;
            CycleNumber currentCycle = currentCycle();
            Money initialPool = eur("10.00");
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, calc, evaluator))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("jackpotId must not be null");
        }

        @Test
        void givenNullCurrentCycle_whenNewInstance_thenThrowException() {
            JackpotId jackpotId = jackpotId();
            CycleNumber currentCycle = null;
            Money initialPool = eur("10.00");
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, calc, evaluator))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("currentCycle must not be null");
        }

        @Test
        void givenNullInitialPool_whenNewInstance_thenThrowException() {
            JackpotId jackpotId = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initialPool = null;
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, calc, evaluator))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("initialPool must not be null");
        }

        @Test
        void givenNullCurrentPool_whenNewInstance_thenThrowException() {
            JackpotId jackpotId = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initialPool = eur("10.00");
            Money currentPool = null;
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, currentPool, calc, evaluator))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("currentPool must not be null");
        }

        @Test
        void givenNullContributionCalculator_whenNewInstance_thenNpe() {
            JackpotId jackpotId = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initialPool = eur("10.00");
            ContributionCalculator calc = null;
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, calc, evaluator))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("contributionCalculator must not be null");
        }

        @Test
        void givenNullRewardEvaluator_whenNewInstance_thenNpe() {
            JackpotId jackpotId = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initialPool = eur("10.00");
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = null;

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, calc, evaluator))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("rewardEvaluator must not be null");
        }

        @Test
        void givenCurrencyMismatchBetweenInitialAndCurrent_whenNewInstance_thenIae() {
            JackpotId jackpotId = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initialPool = eur("10.00");
            Money currentPool = usd("10.00");
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            assertThatThrownBy(() -> new Jackpot(jackpotId, currentCycle, initialPool, currentPool, calc, evaluator))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("initialPool and currentPool must use the same currency");
        }

        @Test
        void whenNewInstance_thenSuccess() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId id = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initial = eur("25.00");
            Money current = eur("30.00");

            Jackpot result = new Jackpot(id, currentCycle, initial, current, calc, evaluator);

            assertThat(result)
                    .isNotNull()
                    .satisfies(j -> {
                        assertThat(j.jackpotId()).isEqualTo(id);
                        assertThat(j.initialPool()).isEqualTo(initial);
                        assertThat(j.currentPool()).isEqualTo(current);
                    });
        }
    }

    // ------------------------------------------------------------------
    // Contribution Tests
    // ------------------------------------------------------------------

    @Nested
    class ContributionTests {

        @Test
        void givenValidBet_whenContribute_thenUsesCalculatorAndIncrementsPool() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId id = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initial = eur("100.00");
            Money current = eur("120.00");
            Jackpot jackpot = new Jackpot(id, currentCycle, initial, current, calc, evaluator);

            Bet bet = new Bet(betId(), userId(), id, eur("10.00"));

            given(calc.calculate(any(ContributionContext.class))).willReturn(eur("2.50"));

            JackpotContribution result = jackpot.contribute(bet);

            then(calc).should(times(1)).calculate(contributionCtxCaptor.capture());

            ContributionContext ctx = contributionCtxCaptor.getValue();
            assertThat(ctx).satisfies(c -> {
                assertThat(c.betAmount()).isEqualTo(eur("10.00"));
                assertThat(c.currentPool()).isEqualTo(current);
                assertThat(c.initialPool()).isEqualTo(initial);
            });

            then(evaluator).shouldHaveNoInteractions();

            assertThat(jackpot.currentPool()).isEqualTo(eur("122.50"));
            assertThat(result)
                    .isNotNull()
                    .satisfies(jc -> {
                        assertThat(jc.betId()).isEqualTo(bet.betId());
                        assertThat(jc.userId()).isEqualTo(bet.userId());
                        assertThat(jc.jackpotId()).isEqualTo(id);
                        assertThat(jc.jackpotCycle()).isEqualTo(currentCycle);
                        assertThat(jc.stakeAmount()).isEqualTo(eur("10.00"));
                        assertThat(jc.contributionAmount()).isEqualTo(eur("2.50"));
                        assertThat(jc.currentJackpotAmount()).isEqualTo(eur("122.50"));
                        assertThat(jc.createdAt()).isNotNull();
                    });
        }

        @Test
        void givenNullBet_whenContribute_thenThrowException() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            Jackpot jackpot = new Jackpot(jackpotId(), currentCycle(), eur("10.00"), calc, evaluator);

            assertThatThrownBy(() -> jackpot.contribute(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("bet must not be null");

            then(calc).shouldHaveNoInteractions();
            then(evaluator).shouldHaveNoInteractions();
        }

        @Test
        void givenOtherJackpotBet_whenContribute_thenThrowException() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId target = jackpotId();
            Jackpot jackpot = new Jackpot(target, currentCycle(), eur("10.00"), calc, evaluator);

            Bet bet = new Bet(betId(), userId(), jackpotId(), eur("5.00"));

            assertThatThrownBy(() -> jackpot.contribute(bet))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("bet targets another jackpot");

            then(calc).shouldHaveNoInteractions();
            then(evaluator).shouldHaveNoInteractions();
        }

        @Test
        void givenCurrencyMismatch_whenContribute_thenThrowException() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId id = jackpotId();
            Jackpot jackpot = new Jackpot(id, currentCycle(), eur("10.00"), calc, evaluator);

            Bet bet = new Bet(betId(), userId(), id, usd("1.00")); // USD vs EUR

            assertThatThrownBy(() -> jackpot.contribute(bet))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("betAmount currency must equal jackpot currency");

            then(calc).shouldHaveNoInteractions();
            then(evaluator).shouldHaveNoInteractions();
        }
    }

    // ------------------------------------------------------------------
    // Reward Tests
    // ------------------------------------------------------------------

    @Nested
    class RewardTests {

        @Test
        void givenNullBet_whenEvaluate_thenThrowException() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            Jackpot jackpot = new Jackpot(jackpotId(), currentCycle(), eur("10.00"), calc, evaluator);

            assertThatThrownBy(() -> jackpot.evaluateRewardFor(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("bet must not be null");

            then(evaluator).shouldHaveNoInteractions();
            then(calc).shouldHaveNoInteractions();
        }

        @Test
        void givenOtherJackpotBet_whenEvaluate_thenThrowException() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId target = jackpotId();
            Jackpot jackpot = new Jackpot(target, currentCycle(), eur("10.00"), calc, evaluator);

            Bet bet = new Bet(betId(), userId(), jackpotId(), eur("2.00")); // different id

            assertThatThrownBy(() -> jackpot.evaluateRewardFor(bet))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("bet targets another jackpot");

            then(evaluator).shouldHaveNoInteractions();
            then(calc).shouldHaveNoInteractions();
        }

        @Test
        void givenEvaluatorReturnsFalse_whenEvaluate_thenEmptyAndNoPoolChange() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId id = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initial = eur("50.00");
            Money current = eur("120.00");
            Jackpot jackpot = new Jackpot(id, currentCycle, initial, current, calc, evaluator);

            Bet bet = new Bet(betId(), userId(), id, eur("2.00"));

            given(evaluator.evaluate(any(RewardContext.class))).willReturn(false);

            Money currentPoolBeforeEvaluation = jackpot.currentPool();

            Optional<JackpotReward> result = jackpot.evaluateRewardFor(bet);

            then(evaluator).should(times(1)).evaluate(rewardCtxCaptor.capture());
            then(calc).shouldHaveNoInteractions();

            RewardContext ctx = rewardCtxCaptor.getValue();
            assertThat(ctx.currentPool()).isEqualTo(current);

            assertThat(result).isEmpty();
            assertThat(jackpot.currentPool()).isEqualTo(currentPoolBeforeEvaluation);
        }

        @Test
        void givenEvaluatorTrue_whenEvaluate_thenRewardAndNoPoolChange() {
            ContributionCalculator calc = mock(ContributionCalculator.class);
            RewardEvaluator evaluator = mock(RewardEvaluator.class);

            JackpotId id = jackpotId();
            CycleNumber currentCycle = currentCycle();
            Money initial = eur("50.00");
            Money current = eur("120.00");
            Jackpot jackpot = new Jackpot(id, currentCycle, initial, current, calc, evaluator);

            Bet bet = new Bet(betId(), userId(), id, eur("2.00"));

            given(evaluator.evaluate(any(RewardContext.class))).willReturn(true);

            Optional<JackpotReward> result = jackpot.evaluateRewardFor(bet);

            then(evaluator).should(times(1)).evaluate(rewardCtxCaptor.capture());
            then(calc).shouldHaveNoInteractions();

            RewardContext ctx = rewardCtxCaptor.getValue();
            assertThat(ctx.currentPool()).isEqualTo(current);

            assertThat(result).isPresent();
            JackpotReward reward = result.orElseThrow();

            assertThat(reward)
                    .satisfies(r -> {
                        assertThat(r.betId()).isEqualTo(bet.betId());
                        assertThat(r.userId()).isEqualTo(bet.userId());
                        assertThat(r.jackpotId()).isEqualTo(id);
                        assertThat(r.jackpotCycle()).isEqualTo(currentCycle);
                        assertThat(r.rewardAmount()).isEqualTo(current);
                        assertThat(r.createdAt()).isNotNull();
                    });

            assertThat(jackpot.currentPool()).isEqualTo(current);
        }
    }

    @Test
    void whenStartNextCycle_thenCycleIncrementedAndPoolResetToInitialAmount() {
        ContributionCalculator calc = mock(ContributionCalculator.class);
        RewardEvaluator evaluator = mock(RewardEvaluator.class);

        JackpotId id = jackpotId();
        CycleNumber currentCycle = currentCycle();
        Money initial = eur("50.00");
        Money current = eur("120.00");
        Jackpot jackpot = new Jackpot(id, currentCycle, initial, current, calc, evaluator);

        jackpot.startNextCycle();

        assertThat(jackpot.currentPool()).isEqualTo(initial);
        assertThat(jackpot.currentCycle().value()).isEqualTo(currentCycle.value() + 1);
    }

    // ------------------------------------------------------------------
    // Fixtures / Helpers
    // ------------------------------------------------------------------

    private static BetId betId() {
        return BetId.of(UUID.randomUUID());
    }

    private static UserId userId() {
        return UserId.of(UUID.randomUUID());
    }

    private static JackpotId jackpotId() {
        return JackpotId.of(UUID.randomUUID());
    }

    private static CycleNumber currentCycle() {
        return CycleNumber.of(1);
    }

    private static Money eur(String amount) {
        return Money.of(amount, "EUR");
    }

    private static Money usd(String amount) {
        return Money.of(amount, "USD");
    }
}

