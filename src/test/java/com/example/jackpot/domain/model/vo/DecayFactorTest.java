package com.example.jackpot.domain.model.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DecayFactorTest {

    @Test
    void givenNullBigDecimal_whenOf_thenThrowNpe() {
        assertThatThrownBy(() -> DecayFactor.of((BigDecimal) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void givenNullString_whenOf_thenThrowNpe() {
        assertThatThrownBy(() -> DecayFactor.of((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value must not be null");
    }

    @ParameterizedTest(name = "[{index}] invalid={0} → IllegalArgumentException (must be > 0)")
    @ValueSource(strings = {"0", "0.00", "-0.00000001", "-1", "-100"})
    void givenNonPositiveValue_whenOf_thenThrowException(String value) {
        assertThatThrownBy(() -> DecayFactor.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value must be > 0");
    }

    @ParameterizedTest(name = "[{index}] input={0} → ok, stripped={1}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    INPUT,       EXPECTED
                    0.0000001,   0.0000001
                    1,           1
                    1.0,         1
                    1.2300,      1.23
                    10.5000,     10.5
                    """
    )
    void givenPositiveValue_whenOf_thenCreatedAndStripped(String input, String expected) {
        DecayFactor result = DecayFactor.of(input);

        assertThat(result).isNotNull();
        assertThat(result.value()).isEqualByComparingTo(expected);
    }

    @Test
    void givenEquivalentValues_whenEqualsHashCode_thenMatch() {
        DecayFactor a = DecayFactor.of("1.2300");
        DecayFactor b = DecayFactor.of("1.23");

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }
}