package com.example.jackpot.adapter.out.persistence.jpa.config.contribution;

import com.example.jackpot.domain.contribution.ContributionCalculator;
import com.example.jackpot.domain.contribution.FixedContributionCalculator;
import com.example.jackpot.domain.contribution.VariableContributionCalculator;
import com.example.jackpot.domain.model.vo.DecayFactor;
import com.example.jackpot.domain.model.vo.Percentage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.Objects.requireNonNull;

public final class ContributionConfigMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ContributionConfigMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
    }

    public static ContributionCalculator toDomain(String json) {
        requireNonNull(json, "json cannot be null");

        try {
            var root = MAPPER.readTree(json);
            var type = root.path("type").asText();
            var cfg = root.path("config");

            return switch (type.toUpperCase()) {
                case "FIXED" -> {
                    FixedContributionConfigJson config = MAPPER.treeToValue(cfg, FixedContributionConfigJson.class);
                    yield new FixedContributionCalculator(Percentage.of(config.rate()));
                }
                case "VARIABLE" -> {
                    VariableContributionConfigJson config = MAPPER.treeToValue(cfg, VariableContributionConfigJson.class);
                    yield new VariableContributionCalculator(
                            Percentage.of(config.startingRate()),
                            Percentage.of(config.minimumRate()),
                            DecayFactor.of(config.decayFactor())
                    );
                }
                default -> throw new IllegalArgumentException("Unsupported contribution type: '%s'".formatted(type));
            };
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse reward config JSON", e);
        }
    }
}
