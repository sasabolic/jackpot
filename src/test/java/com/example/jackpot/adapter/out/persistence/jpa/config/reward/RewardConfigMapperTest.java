package com.example.jackpot.adapter.out.persistence.jpa.config.reward;

import com.example.jackpot.domain.reward.FixedChanceRewardEvaluator;
import com.example.jackpot.domain.reward.RewardEvaluator;
import com.example.jackpot.domain.reward.VariableChanceRewardEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;

class RewardConfigMapperTest {

    @Test
    void givenNullJson_whenToDomain_thenThrowException() {
        assertThatThrownBy(() -> RewardConfigMapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("json must not be null");
    }

    @Test
    void givenUnknownType_whenToDomain_thenThrowException() {
        String json = """
                { "type": "SOMETHING_ELSE", "schemaVersion": 1, "config": { "chancePercent": "2.50" } }
                """;

        assertThatThrownBy(() -> RewardConfigMapper.toDomain(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported reward type: 'SOMETHING_ELSE'");
    }

    @Test
    void givenMissingType_whenToDomain_thenThrowException() {
        String json = """
                { "schemaVersion": 1, "config": { "chancePercent": "2.50" } }
                """;

        assertThatThrownBy(() -> RewardConfigMapper.toDomain(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported reward type: ''");
    }

    @Test
    void givenInvalidJson_whenToDomain_thenThrowException() {
        String json = "{ this is not valid json";

        assertThatThrownBy(() -> RewardConfigMapper.toDomain(json))
                .isInstanceOf(IllegalStateException.class);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validFixedChanceRewardConfigJson")
    void givenFixedChanceConfigs_whenToDomain_thenReturnsFixedEvaluator(String json) {
        RewardEvaluator result = RewardConfigMapper.toDomain(json);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(FixedChanceRewardEvaluator.class);
    }


    @Test
    void givenVariableChanceWithStringValues_whenToDomain_thenSuccess() {
        String json = """
                {
                  "type": "VARIABLE_CHANCE",
                  "schemaVersion": 1,
                  "config": {
                    "startPercent": "1.00",
                    "rewardPoolLimit": { "amount": "1000.00", "currency": "EUR" }
                  }
                }
                """;

        RewardEvaluator result = RewardConfigMapper.toDomain(json);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(VariableChanceRewardEvaluator.class);
    }

    @Test
    void givenVariableChanceWithNumericValues_whenToDomain_thenSuccess() {
        String json = """
                {
                  "type": "VARIABLE_CHANCE",
                  "schemaVersion": 1,
                  "config": {
                    "startPercent": 1.00,
                    "rewardPoolLimit": { "amount": 1000.00, "currency": "EUR" }
                  }
                }
                """;

        RewardEvaluator result = RewardConfigMapper.toDomain(json);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(VariableChanceRewardEvaluator.class);
    }

    // ----------------------------------
    // Test Data
    // ----------------------------------

    static Stream<Arguments> validFixedChanceRewardConfigJson() {
        return Stream.of(
                Arguments.of(named("string percent",
                        """
                                { "type": "FIXED_CHANCE", "schemaVersion": 1, "config": { "chancePercent": "2.50" } }
                                """
                )),
                Arguments.of(named("numeric percent",
                        """
                                { "type": "FIXED_CHANCE", "schemaVersion": 1, "config": { "chancePercent": 2.50 } }
                                """
                )),
                Arguments.of(named("schemaVersion ignored",
                        """
                                { "type": "FIXED_CHANCE", "schemaVersion": 99, "config": { "chancePercent": "0.75" } }
                                """
                ))
        );
    }
}
