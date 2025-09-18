package com.example.jackpot.domain.model.vo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @ParameterizedTest(name = "[{index}] given amount={0} and currency={1} → {2} {3}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    AMOUNT,     CURRENCY_CODE,      EXPECTED_AMOUNT,    EXPECTED_CURRENCY_CODE
                    1,          EUR,                1.00,               EUR
                    0,          EUR,                0.00,               EUR
                    1.234,      EUR,                1.23,               EUR
                    1.235,      USD,                1.24,               USD
                    10.005,     RSD,                10.01,              RSD
                    -12.00,     GBP,                -12.00,             GBP
                    """
    )
    void givenValidAmountAndCurrency_whenOf_thenNormalizedAndCurrencySet(String amount, String currencyCode, String expectedAmount, String expectedCurrencyCode) {
        Money m = Money.of(amount, currencyCode);

        assertThat(m.amount()).isEqualByComparingTo(new BigDecimal(expectedAmount));
        assertThat(m.currency()).isEqualTo(Currency.getInstance(expectedCurrencyCode));
    }

    @Test
    void givenInvalidStringAmount_whenOf_thenThrowException() {
        assertThatThrownBy(() -> Money.of("not-a-number", "EUR"))
                .isInstanceOf(NumberFormatException.class);
    }

    @Test
    void givenInvalidStringCurrencyCode_whenOf_thenThrowException() {
        assertThatThrownBy(() -> Money.of("1.00", "ZZZ1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenAmountIsNull_whenOf_thenThrowException() {
        assertThatThrownBy(() -> Money.of(null, "EUR"))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void givenCurrencyIsNull_whenOf_thenThrowException() {
        assertThatThrownBy(() -> Money.of("1.00", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void givenEquivalentInputs_whenOf_thenEqualsBigDecimalOverload() {
        String amount = "1.234";
        String code = "EUR";

        Money fromString = Money.of(amount, code);
        Money fromBigDecimalAndCurrency = Money.of(new BigDecimal(amount), Currency.getInstance(code));

        assertThat(fromString).isEqualTo(fromBigDecimalAndCurrency);
    }

    @ParameterizedTest(name = "[{index}] given amount={0} → isPositive={1}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    AMOUNT,  EXPECTED
                    0,       false
                    0.00,    false
                    0.001,   false
                    0.004,   false
                    0.005,   true
                    0.01,    true
                    10.00,   true
                    -0.004,  false
                    -0.005,  false
                    -1.00,   false
                    """
    )
    void givenAmount_whenIsPositive_thenMatchesExpectation(String amount, boolean expected) {
        Money m = Money.of(amount, "EUR");

        assertThat(m.isPositive()).isEqualTo(expected);
    }

    @ParameterizedTest(name = "[{index}] given amount={0} → isZero={1}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    AMOUNT,  EXPECTED
                    0,       true
                    0.00,    true
                    0.001,   true
                    0.004,   true
                    0.005,   false
                    -0.004,  true
                    -0.005,  false
                    1.00,    false
                    -1.00,   false
                    """
    )
    void givenAmount_whenIsZero_thenMatchesExpectation(String amount, boolean expected) {
        Money m = Money.of(amount, "EUR");

        assertThat(m.isZero()).isEqualTo(expected);
    }

    @Test
    void givenSameCurrency_whenHasSameCurrencyAs_thenTrue() {
        Money a = Money.of("0.01", "EUR");
        Money b = Money.of("123.45", "EUR");

        assertThat(a.hasSameCurrencyAs(b)).isTrue();
    }

    @Test
    void givenDifferentCurrency_whenHasSameCurrencyAs_thenFalse() {
        Money a = Money.of("0.01", "EUR");
        Money b = Money.of("0.02", "USD");

        assertThat(a.hasSameCurrencyAs(b)).isFalse();
    }

    @Test
    void givenDifferentCurrencies_whenHasSameCurrencyAs_thenFalse() {
        Money a = Money.of("0.01", "EUR");
        Money b = Money.of("0.02", "EUR");
        Money c = Money.of("0.02", "USD");

        assertThat(a.hasSameCurrencyAs(b, c)).isFalse();
    }

    @Test
    void givenNull_whenHasSameCurrencyAs_thenFalse() {
        Money a = Money.of("0.01", "EUR");

        assertThat(a.hasSameCurrencyAs((Money) null)).isFalse();
    }

    @ParameterizedTest(name = "[{index}] given check if {0} > {1} (EUR) → {2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    LEFT,  RIGHT, EXPECTED
                    2.00,  1.99,  true
                    1.99,  2.00,  false
                    2.00,  2.00,  false
                    """
    )
    void givenSameCurrency_whenIsGreaterThan_thenMatches(String left, String right, boolean expected) {
        Money a = Money.of(left, "EUR");
        Money b = Money.of(right, "EUR");

        assertThat(a.isGreaterThan(b)).isEqualTo(expected);
    }

    @Test
    void givenDifferentCurrencies_whenIsGreaterThan_thenThrowException() {
        Money eur = Money.of("2.00", "EUR");
        Money usd = Money.of("1.00", "USD");

        assertThatThrownBy(() -> eur.isGreaterThan(usd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currencies must be equal");
    }

    @ParameterizedTest(name = "[{index}] given {0} + {1} → {2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    LEFT,   RIGHT,  EXPECTED
                    10.00,  2.50,   12.50
                    0.01,   0.02,   0.03
                    1.10,   1.10,   2.20
                    -1.00,  0.50,   -0.50
                    0.00,   0.00,   0.00
                    """
    )
    void givenSameCurrency_whenPlus_thenCorrectResult(String left, String right, String expected) {
        Money a = Money.of(left, "EUR");
        Money b = Money.of(right, "EUR");

        Money result = a.plus(b);

        assertThat(result).isEqualTo(Money.of(expected, "EUR"));
        assertThat(result.currency()).isEqualTo(Currency.getInstance("EUR"));
    }

    @Test
    void givenDifferentCurrencies_whenPlus_thenThrowException() {
        Money eur = Money.of("10.00", "EUR");
        Money usd = Money.of("1.00", "USD");

        assertThatThrownBy(() -> eur.plus(usd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currencies must be equal");
    }

    @Test
    void givenOtherIsNull_whenPlus_thenThrowException() {
        Money eur = Money.of("10.00", "EUR");

        assertThatThrownBy(() -> eur.plus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "[{index}] given {0} - {1} → {2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    LEFT,   RIGHT,  EXPECTED
                    10.00,  2.50,   7.50
                    5.00,   5.00,   0.00
                    0.10,   0.30,   -0.20
                    -1.00,  0.50,   -1.50
                    """
    )
    void givenSameCurrency_whenMinus_thenCorrectResult(String left, String right, String expected) {
        Money a = Money.of(left, "EUR");
        Money b = Money.of(right, "EUR");

        Money result = a.minus(b);

        assertThat(result).isEqualTo(Money.of(expected, "EUR"));
        assertThat(result.currency()).isEqualTo(Currency.getInstance("EUR"));
    }

    @Test
    void givenDifferentCurrencies_whenMinus_thenThrowException() {
        Money eur = Money.of("1.00", "EUR");
        Money usd = Money.of("1.00", "USD");

        assertThatThrownBy(() -> eur.minus(usd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currencies must be equal");
    }

    @Test
    void givenNullOther_whenMinus_thenThrowException() {
        Money eur = Money.of("10.00", "EUR");
        assertThatThrownBy(() -> eur.minus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "[{index}] given {0} * {1} → {2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    AMOUNT, FACTOR, EXPECTED
                    10.00,  0.333,  3.33
                    1.005,  1,      1.01
                    2.50,   0.2,    0.50
                    2.00,   -3,     -6.00
                    """
    )
    void givenFactor_whenTimes_thenCorrectResult(String amount, String factor, String expected) {
        Money m = Money.of(amount, "EUR");

        assertThat(m.times(new BigDecimal(factor))).isEqualTo(Money.of(expected, "EUR"));
    }

    @Test
    void givenNullFactor_whenTimes_thenThrowException() {
        Money m = Money.of("1.00", "EUR");

        assertThatThrownBy(() -> m.times(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("factor must not be null");
    }

    @ParameterizedTest(name = "[{index}] given {0} / {1} → {2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    AMOUNT, DIVISOR, EXPECTED
                    10.00,  3,       3.33
                    10.00,  2,       5.00
                    1.00,   4,       0.25
                    """
    )
    void givenPositiveDivisor_whenDivide_thenCorrectResult(String amount, String divisor, String expected) {
        Money m = Money.of(amount, "EUR");

        assertThat(m.divide(new BigDecimal(divisor))).isEqualTo(Money.of(expected, "EUR"));
    }

    @Test
    void givenZeroDivisor_whenDivide_thenThrowException() {
        Money m = Money.of("10.00", "EUR");
        assertThatThrownBy(() -> m.divide(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void givenNullDivisor_whenDivide_thenThrowException() {
        Money m = Money.of("10.00", "EUR");

        assertThatThrownBy(() -> m.divide(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("divisor must not be null");
    }

    @ParameterizedTest(name = "[{index}] compareTo: {0} ? {1} (EUR) → sign {2}")
    @CsvSource(
            useHeadersInDisplayName = true,
            textBlock = """
                    LEFT,  RIGHT, EXPECTED
                    1.00,  2.00,  -1
                    2.00,  1.00,   1
                    2.00,  2.00,   0
                    """
    )
    void givenSameCurrency_whenCompareTo_thenOrders(String left, String right, int expected) {
        Money a = Money.of(left, "EUR");
        Money b = Money.of(right, "EUR");

        int result = a.compareTo(b);

        assertThat(Integer.signum(result)).isEqualTo(expected);
    }

    @Test
    void givenDifferentCurrencies_whenCompareTo_thenThrowException() {
        Money eur = Money.of("1.00", "EUR");
        Money usd = Money.of("1.00", "USD");

        assertThatThrownBy(() -> eur.compareTo(usd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Currencies must be equal");
    }
}