package com.example.jackpot.domain.model.vo;


import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentageTest {

    // --------------------------------------------------------------------
    // Factory / Constructor Tests
    // --------------------------------------------------------------------

    @Nested
    class FactoryTests {

        @Test
        void givenNullBigDecimal_whenOf_thenThrowException() {
            assertThatThrownBy(() -> Percentage.of((BigDecimal) null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("value must not be null");
        }

        @Test
        void givenNullString_whenOf_thenThrowException() {
            assertThatThrownBy(() -> Percentage.of((String) null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("value must not be null");
        }

        @ParameterizedTest(name = "[{index}] out-of-range={0} → IAE [0,100]")
        @ValueSource(strings = {"-0.01", "100.01", "-1", "1000"})
        void givenOutOfBounds_whenOf_thenThrowException(String raw) {
            assertThatThrownBy(() -> Percentage.of(raw))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("value must be in [0,100]");
        }

        @ParameterizedTest(name = "[{index}] ok={0}")
        @ValueSource(strings = {"0", "0.00", "1", "50.12345678", "99.99999999", "100", "100.00"})
        void givenBoundaryOrValid_whenOf_thenSuccess(String value) {
            Percentage result = Percentage.of(value);

            assertThat(result).isNotNull();
        }

        @Test
        void givenEquivalentValues_whenEqualsHashCode_thenMatch() {
            Percentage a = Percentage.of("1.0");
            Percentage b = Percentage.of("1.00");

            assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
        }
    }

    // ----------------------------------
    // Accessors Tests
    // ----------------------------------

    @Nested
    class AccessorsTests {

        @ParameterizedTest(name = "[{index}] {0}% → fractional={1} (scale=8, DOWN)")
        @CsvSource(
                useHeadersInDisplayName = true,
                textBlock = """
                        PCT,          EXPECTED_FRACTIONAL
                        0,            0.00000000
                        100,          1.00000000
                        50,           0.50000000
                        33.33333334,  0.33333333
                        66.66666667,  0.66666666
                        """
        )
        void givenPercent_whenFractionalValue_thenRoundedDown(String pct, String expected) {
            Percentage p = Percentage.of(pct);

            BigDecimal result = p.fractionalValue();

            assertThat(result).isEqualByComparingTo(expected);
        }

        @Test
        void givenValue_whenValueAccessor_thenTrailingZerosTrimmed() {
            Percentage p = Percentage.of(new BigDecimal("12.3400"));

            BigDecimal result = p.value();

            assertThat(result).isEqualByComparingTo("12.34");
        }
    }

    // ----------------------------------
    // Comparisons
    // ----------------------------------

    @Nested
    class Comparisons {

        @ParameterizedTest(name = "[{index}] {0}% > {1}% → {2}")
        @CsvSource({
                "2.00, 1.99, true",
                "1.00, 2.00, false",
                "5.00, 5.00, false"
        })
        void givenTwoValues_whenIsGreaterThan_thenCorrect(String a, String b, boolean expected) {
            boolean result = Percentage.of(a).isGreaterThan(Percentage.of(b));

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest(name = "[{index}] {0}% < {1}% → {2}")
        @CsvSource({
                "1.99, 2.00, true",
                "2.00, 1.99, false",
                "5.00, 5.00, false"
        })
        void givenTwoValues_whenIsLessThan_thenCorrect(String a, String b, boolean expected) {
            boolean result = Percentage.of(a).isLessThan(Percentage.of(b));

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest(name = "[{index}] {0}% ≤ {1}% → {2}")
        @CsvSource({
                "1.99, 2.00, true",
                "2.00, 1.99, false",
                "5.00, 5.00, true"
        })
        void givenTwoValues_whenIsLessThanOrEqual_thenCorrect(String a, String b, boolean expected) {
            boolean result = Percentage.of(a).isLessThanOrEqual(Percentage.of(b));

            assertThat(result).isEqualTo(expected);
        }

        @ParameterizedTest(name = "[{index}] {0}% → isHundred={1}")
        @CsvSource(
                useHeadersInDisplayName = true,
                textBlock = """
                        VALUE,        EXPECTED
                        100,          true
                        100.00,       true
                        99.99999999,  false
                        0,            false
                        50,           false
                        """
        )
        void givenValue_whenIsHundred_thenMatchesExpectation(String value, boolean expected) {
            boolean result = Percentage.of(value).isHundred();

            assertThat(result).isEqualTo(expected);
        }
    }

    // --------------------------------------------------------------------
    // Arithmetic Tests: plus / minus / times (with clamping)
    // --------------------------------------------------------------------

    @Nested
    class ArithmeticTests {

        @ParameterizedTest(name = "[{index}] {0}% + {1}% = {2}% (clamped at 100)")
        @CsvSource(
                useHeadersInDisplayName = true,
                textBlock = """
                        A,      B,      EXPECTED
                        10.00,  5.00,   15.00
                        90.00,  15.00,  100.00
                        99.99,  0.02,   100.00
                        0.00,   0.00,   0.00
                        """
        )
        void givenTwoPercents_whenPlus_thenAddedAndClampedAtHundred(String a, String b, String expected) {
            Percentage result = Percentage.of(a).plus(Percentage.of(b));

            assertThat(result.value()).isEqualByComparingTo(expected);
        }

        @ParameterizedTest(name = "[{index}] {0}% - {1}% = {2}% (clamped at 0)")
        @CsvSource(
                useHeadersInDisplayName = true,
                textBlock = """
                        A,      B,      EXPECTED
                        10.00,  5.00,   5.00
                        5.00,   10.00,  0.00
                        0.00,   0.01,   0.00
                        100.00, 100.00, 0.00
                        """
        )
        void givenTwoPercents_whenMinus_thenSubtractedAndClampedAtZero(String a, String b, String expected) {
            Percentage result = Percentage.of(a).minus(Percentage.of(b));

            assertThat(result.value()).isEqualByComparingTo(expected);
        }

        @Test
        void givenNullFactor_whenTimes_thenThrowException() {
            Percentage p = Percentage.of("10.00");

            assertThatThrownBy(() -> p.times(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("factor must not be null");
        }

        @ParameterizedTest(name = "[{index}] negative factor={0} → IAE")
        @ValueSource(strings = {"-0.00000001", "-1", "-100"})
        void givenNegativeFactor_whenTimes_thenThrowException(String factor) {
            Percentage p = Percentage.of("10.00");
            BigDecimal f = new BigDecimal(factor);

            assertThatThrownBy(() -> p.times(f))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("factor must be ≥ 0");
        }

        @ParameterizedTest(name = "[{index}] {0}% * {1} = {2}% (clamped at 100)")
        @CsvSource(
                useHeadersInDisplayName = true,
                textBlock = """
                        BASE,     FACTOR,     EXPECTED
                        25.00,    2,          50.00
                        60.00,    2,          100.00
                        33.33,    3,          99.99
                        0.00,     123.45,     0.00
                        12.34,    0.5,        6.17
                        """
        )
        void givenFactor_whenTimes_thenMultipliedAndClampedAtHundred(String base, String factor, String expected) {
            Percentage result = Percentage.of(base).times(new BigDecimal(factor));

            assertThat(result.value()).isEqualByComparingTo(expected);
        }
    }

}