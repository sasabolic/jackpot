package com.example.jackpot.domain.model;

import com.example.jackpot.domain.contribution.ContributionCalculator;
import com.example.jackpot.domain.contribution.ContributionContext;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.reward.RewardContext;
import com.example.jackpot.domain.reward.RewardEvaluator;

import java.time.Instant;
import java.util.Optional;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

public final class Jackpot {
    private final JackpotId jackpotId;
    private final Money initialPool;
    private Money currentPool;

    private final ContributionCalculator contributionCalculator;
    private final RewardEvaluator rewardEvaluator;

    public Jackpot(JackpotId jackpotId, Money initialPool, ContributionCalculator contributionCalculator, RewardEvaluator rewardEvaluator) {
        this(jackpotId, initialPool, initialPool, contributionCalculator, rewardEvaluator);
    }

    public Jackpot(JackpotId jackpotId, Money initialPool, Money currentPool, ContributionCalculator contributionCalculator, RewardEvaluator rewardEvaluator) {
        requireNonNull(jackpotId, "jackpotId must not be null");
        requireNonNull(initialPool, "initialPool must not be null");
        requireNonNull(currentPool, "currentPool must not be null");
        requireNonNull(contributionCalculator, "contributionCalculator must not be null");
        requireNonNull(rewardEvaluator, "rewardEvaluator must not be null");

        isTrue(initialPool.hasSameCurrencyAs(currentPool), "initialPool and currentPool must use the same currency");

        this.jackpotId = jackpotId;
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

        return new JackpotContribution(bet.betId(), bet.userId(), this.jackpotId, bet.betAmount(), contribution, this.currentPool, Instant.now());
    }

    public Optional<JackpotReward> evaluateRewardFor(Bet bet) {
        requireNonNull(bet, "bet must not be null");

        isTrue(bet.jackpotId().equals(this.jackpotId), "bet targets another jackpot");

        boolean win = rewardEvaluator.evaluate(new RewardContext(this.currentPool));
        if (!win) {
            return Optional.empty();
        }

        Money payout = this.currentPool;
        this.currentPool = this.initialPool;

        return Optional.of(new JackpotReward(
                bet.betId(),
                bet.userId(),
                this.jackpotId,
                payout,
                Instant.now())
        );
    }

    public JackpotId jackpotId() {
        return this.jackpotId;
    }

    public Money initialPool() {
        return this.initialPool;
    }

    public Money currentPool() {
        return this.currentPool;
    }
}
