package com.example.jackpot.domain.model;

import com.example.jackpot.domain.contribution.ContributionCalculator;
import com.example.jackpot.domain.contribution.ContributionContext;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.vo.CycleNumber;
import com.example.jackpot.domain.model.vo.JackpotCycle;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.reward.RewardContext;
import com.example.jackpot.domain.reward.RewardEvaluator;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

/**
 * Aggregate root representing a jackpot and its lifecycle.
 * <p>
 * This class encapsulates the state and core business logic related to a jackpot,
 * including managing its pool amount, handling contributions from bets,
 * evaluating rewards, and starting new jackpot cycles.
 */
@Slf4j
public final class Jackpot {
    private final JackpotId jackpotId;
    private CycleNumber currentCycle;
    private final Money initialPool;
    private Money currentPool;

    private final ContributionCalculator contributionCalculator;
    private final RewardEvaluator rewardEvaluator;

    public Jackpot(JackpotId jackpotId, CycleNumber currentCycle, Money initialPool, ContributionCalculator contributionCalculator, RewardEvaluator rewardEvaluator) {
        this(jackpotId, currentCycle, initialPool, initialPool, contributionCalculator, rewardEvaluator);
    }

    public Jackpot(JackpotId jackpotId, CycleNumber currentCycle, Money initialPool, Money currentPool, ContributionCalculator contributionCalculator, RewardEvaluator rewardEvaluator) {
        requireNonNull(jackpotId, "jackpotId must not be null");
        requireNonNull(currentCycle, "currentCycle must not be null");
        requireNonNull(initialPool, "initialPool must not be null");
        requireNonNull(currentPool, "currentPool must not be null");
        requireNonNull(contributionCalculator, "contributionCalculator must not be null");
        requireNonNull(rewardEvaluator, "rewardEvaluator must not be null");

        isTrue(initialPool.hasSameCurrencyAs(currentPool), "initialPool and currentPool must use the same currency");

        this.jackpotId = jackpotId;
        this.currentCycle = currentCycle;
        this.initialPool = initialPool;
        this.currentPool = currentPool;
        this.contributionCalculator = contributionCalculator;
        this.rewardEvaluator = rewardEvaluator;
    }

    public JackpotContribution contribute(Bet bet) {
        requireNonNull(bet, "bet must not be null");

        isTrue(bet.jackpotId().equals(this.jackpotId), "bet targets another jackpot");
        isTrue(bet.betAmount().hasSameCurrencyAs(this.currentPool), "betAmount currency must equal jackpot currency");
        isTrue(bet.betAmount().isPositive(), "betAmount must be positive");

        Money contribution = contributionCalculator.calculate(new ContributionContext(bet.betAmount(), this.currentPool, this.initialPool));

        this.currentPool = this.currentPool.plus(contribution);

        log.info("Added contribution={} for jackpotId={} cycle={}", contribution, this.jackpotId.value(), this.currentCycle.value());

        return new JackpotContribution(
                bet.betId(),
                bet.userId(),
                JackpotCycle.of(this.jackpotId, this.currentCycle),
                bet.betAmount(),
                contribution,
                this.currentPool,
                Instant.now()
        );
    }

    public Optional<JackpotReward> evaluateRewardFor(Bet bet) {
        requireNonNull(bet, "bet must not be null");

        isTrue(bet.jackpotId().equals(this.jackpotId), "bet targets another jackpot");

        boolean win = rewardEvaluator.evaluate(new RewardContext(this.currentPool));

        log.info("Bet with betId={} for jackpotId={} cycle={} evaluated for reward with result={}", bet.betId().value(), this.jackpotId.value(), this.currentCycle.value(), win ? "win" : "loss");

        if (!win) {
            return Optional.empty();
        }

        Money rewardAmount = this.currentPool;

        return Optional.of(new JackpotReward(
                bet.betId(),
                bet.userId(),
                JackpotCycle.of(this.jackpotId, this.currentCycle),
                rewardAmount,
                Instant.now())
        );
    }

    public void startNextCycle() {
        this.currentCycle = this.currentCycle.next();
        this.currentPool = this.initialPool;

        log.info("Started next jackpotId={} cycle={} with a currentPool={}", this.jackpotId.value(), this.currentCycle.value(), this.currentPool);
    }

    public JackpotId jackpotId() {
        return this.jackpotId;
    }

    public CycleNumber currentCycle() {
        return this.currentCycle;
    }

    public Money initialPool() {
        return this.initialPool;
    }

    public Money currentPool() {
        return this.currentPool;
    }
}
