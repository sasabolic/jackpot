package com.example.jackpot.adapter.out.persistence.jpa.config.reward;

import com.example.jackpot.adapter.out.persistence.jpa.config.shared.MoneyJson;

public record VariableRewardConfigJson(String startPercent, MoneyJson rewardPoolLimit) {
}
