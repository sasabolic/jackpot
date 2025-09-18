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
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
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
                    MoneyJson minPool = config.minPool();
                    MoneyJson maxPool = config.maxPool();

                    yield new VariableChanceRewardEvaluator(
                            Percentage.of(config.minPercent()),
                            Percentage.of(config.maxPercent()),
                            Money.of(minPool.amount(), minPool.currency()),
                            Money.of(maxPool.amount(), maxPool.currency())
                    );
                }
                default -> throw new IllegalArgumentException("Unsupported reward type: '%s'".formatted(type));
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse reward config JSON", e);
        }
    }
}
