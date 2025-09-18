package com.example.jackpot.domain.model.vo;

import com.example.jackpot.domain.model.id.JackpotId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JackpotCycleTest {

    @Test
    void givenNullJackpotId_whenOf_thenThrowException() {
        JackpotId jackpotId = null;
        CycleNumber jackpotCycle = CycleNumber.of(1);

        assertThatThrownBy(() -> JackpotCycle.of(jackpotId, jackpotCycle))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("jackpotId must not be null");
    }

    @Test
    void givenNullCycle_whenOf_thenThrowException() {
        JackpotId jackpotId = JackpotId.of(UUID.randomUUID());
        CycleNumber jackpotCycle = null;

        assertThatThrownBy(() -> JackpotCycle.of(jackpotId, jackpotCycle))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("cycle must not be null");
    }

    @Test
    void whenOf_thenThrowException() {
        JackpotId jackpotId = JackpotId.of(UUID.randomUUID());
        CycleNumber jackpotCycle = CycleNumber.of(1);

        JackpotCycle result = JackpotCycle.of(jackpotId, jackpotCycle);

        assertThat(result)
                .isNotNull()
                .satisfies(c -> {
                    assertThat(c.jackpotId()).isEqualTo(jackpotId);
                    assertThat(c.cycle()).isEqualTo(jackpotCycle);
                });
    }
}