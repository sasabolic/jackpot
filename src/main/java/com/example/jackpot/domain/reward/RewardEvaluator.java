package com.example.jackpot.domain.reward;

/**
 * Strategy interface for deciding whether a bet wins a jackpot reward.
 */
public interface RewardEvaluator {
    boolean evaluate(RewardContext ctx);
}
