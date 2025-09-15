package com.example.jackpot.domain.reward;

import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RewardContextTest {

    @ParameterizedTest(name = "[{index}] currentPool={0} EUR → ok")
    @ValueSource(strings = {
            "0.005",   // rounds to 0.01
            "0.01",
            "1.23",
            "100.00"
    })
    void givenPositiveCurrentPool_whenNewInstance_thenSuccess(String amount) {
        Money pool = Money.of(amount, "EUR");

        RewardContext result = new RewardContext(pool);

        assertThat(result.currentPool()).isEqualTo(pool);
    }

    @Test
    void givenNullCurrentPool_whenNewInstance_thenThrowException() {
        assertThatThrownBy(() -> new RewardContext(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("currentPool must not be null");
    }

    @ParameterizedTest(name = "[{index}] currentPool={0} EUR → IAE (must be ≥ 0)")
    @ValueSource(strings = {
            "0",       // zero
            "0.00",    // zero explicit
            "0.004",   // rounds to 0.00
            "-0.004",  // rounds to 0.00, compareTo==0 → not > 0
            "-0.005",  // rounds to -0.01
            "-0.01",
            "-1.00"
    })
    void givenNegativeOrZeroCurrentPool_whenNewInstance_thenThrowException(String amount) {
        Money pool = Money.of(amount, "EUR");

        assertThatThrownBy(() -> new RewardContext(pool))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("currentPool must be > 0");
    }
}