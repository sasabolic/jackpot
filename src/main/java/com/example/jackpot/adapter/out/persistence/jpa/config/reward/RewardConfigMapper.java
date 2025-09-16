package com.example.jackpot.adapter.out.persistence.jpa.config.reward;

import com.example.jackpot.adapter.out.persistence.jpa.config.shared.MoneyJson;
import com.example.jackpot.domain.model.vo.Money;
import com.example.jackpot.domain.model.vo.Percentage;
import com.example.jackpot.domain.reward.FixedChanceRewardEvaluator;
import com.example.jackpot.domain.reward.RewardEvaluator;
import com.example.jackpot.domain.reward.VariableChanceRewardEvaluator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Objects.requireNonNull;

public final class RewardConfigMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RewardConfigMapper() {
    }

    public static RewardEvaluator toDomain(String json) {
        requireNonNull(json, "json must not be null");

        try {
            var root = MAPPER.readTree(json);
            var type = root.path("type").asText();
            var cfg = root.path("config");

            return switch (type.toUpperCase()) {
                case "FIXED_CHANCE" -> {
                    FixedRewardConfigJson config = MAPPER.treeToValue(cfg, FixedRewardConfigJson.class);

                    yield new FixedChanceRewardEvaluator(Percentage.of(config.chancePercent()));
                }
                case "VARIABLE_CHANCE" -> {
                    VariableRewardConfigJson config = MAPPER.treeToValue(cfg, VariableRewardConfigJson.class);
                    MoneyJson rewardPoolLimit = config.rewardPoolLimit();

                    yield new VariableChanceRewardEvaluator(
                            Percentage.of(config.startPercent()),
                            Money.of(rewardPoolLimit.amount(), rewardPoolLimit.currency())
                    );
                }
                default -> throw new IllegalArgumentException("Unsupported reward type: '%s'".formatted(type));
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse reward config JSON", e);
        }
    }
}
