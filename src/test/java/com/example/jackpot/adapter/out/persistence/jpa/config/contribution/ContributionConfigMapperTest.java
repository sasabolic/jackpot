package com.example.jackpot.adapter.out.persistence.jpa.config.contribution;

import com.example.jackpot.domain.contribution.ContributionCalculator;
import com.example.jackpot.domain.contribution.FixedContributionCalculator;
import com.example.jackpot.domain.contribution.VariableContributionCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Named.named;

class ContributionConfigMapperTest {

    @Test
    void givenNullJson_whenToDomain_thenThrowException() {
        assertThatThrownBy(() -> ContributionConfigMapper.toDomain(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("json cannot be null");
    }

    @Test
    void givenUnknownType_whenToDomain_thenThrowException() {
        String json = """
                { "type": "SOMETHING_ELSE", "schemaVersion": 1, "config": { "rate": "5.00" } }
                """;

        assertThatThrownBy(() -> ContributionConfigMapper.toDomain(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported contribution type: 'SOMETHING_ELSE'");
    }

    @Test
    void givenMissingType_whenToDomain_thenThrowException() {
        String json = """
                { "schemaVersion": 1, "config": { "rate": "5.00" } }
                """;

        assertThatThrownBy(() -> ContributionConfigMapper.toDomain(json))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported contribution type: ''");
    }

    @Test
    void givenInvalidJson_whenToDomain_thenThrowException() {
        String json = "{ this is not valid json";

        assertThatThrownBy(() -> ContributionConfigMapper.toDomain(json))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void givenFixedTypeAndStringRate_whenToDomain_thenSuccess() {
        String json = """
                { "type": "FIXED", "schemaVersion": 1, "config": { "rate": "5.00" } }
                """;

        ContributionCalculator result = ContributionConfigMapper.toDomain(json);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(FixedContributionCalculator.class);
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("validFixedContributionConfigJson")
    void givenFixedConfigs_whenToDomain_thenReturnsFixedCalculator(String json) {
        ContributionCalculator result = ContributionConfigMapper.toDomain(json);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(FixedContributionCalculator.class);
    }

    @Test
    void givenVariableTypeAndNumericValues_whenToDomain_thenSuccess() {
        String json = """
                {
                  "type": "VARIABLE",
                  "schemaVersion": 1,
                  "config": {
                    "startingRate": 8.00,
                    "minimumRate":  2.00,
                    "decayFactor":  0.15
                  }
                }
                """;

        ContributionCalculator result = ContributionConfigMapper.toDomain(json);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(VariableContributionCalculator.class);
    }

    // ----------------------------------
    // Test Data
    // ----------------------------------

    static Stream<Arguments> validFixedContributionConfigJson() {
        return Stream.of(
                Arguments.of(
                        named("string rate",
                                """
                                        { "type": "FIXED", "schemaVersion": 1, "config": { "rate": "5.00" } }
                                        """
                        )),
                Arguments.of(
                        named("numeric rate",
                                """
                                        { "type": "FIXED", "schemaVersion": 1, "config": { "rate": 5.00 } }
                                        """
                        )),
                Arguments.of(
                        named("schemaVersion ignored",
                                """
                                        { "type": "FIXED", "schemaVersion": 99, "config": { "rate": "3.33" } }
                                        """
                        ))
        );
    }
}
