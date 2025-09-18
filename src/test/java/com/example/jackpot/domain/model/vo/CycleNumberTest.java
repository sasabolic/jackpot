package com.example.jackpot.domain.model.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CycleNumberTest {

    @Test
    void givenInvalidCycleValue_whenOf_thenThrowException() {
        assertThatThrownBy(() -> CycleNumber.of(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cycle number must be >= 1");
    }

    @Test
    void givenValidCycleValue_whenOf_thenSuccess() {
        CycleNumber result = CycleNumber.of(1);

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(1);
    }

    @Test
    void givenExistingCycleValue_whenNext_thenSuccess() {
        CycleNumber cycleNumber = CycleNumber.of(5);

        CycleNumber result = cycleNumber.next();

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualTo(6);
    }
}