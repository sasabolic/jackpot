package com.example.jackpot.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

@EqualsAndHashCode
@ToString
public final class Percentage implements Comparable<Percentage> {

    private static final BigDecimal ZERO_BIG_DECIMAL = BigDecimal.ZERO;
    private static final BigDecimal HUNDRED_BIG_DECIMAL = new BigDecimal("100");

    private static final int DEFAULT_PROBABILITY_SCALE = 8;
    private static final RoundingMode DEFAULT_PROBABILITY_ROUNDING = RoundingMode.DOWN;

    public static final Percentage ZERO = Percentage.of(ZERO_BIG_DECIMAL);
    public static final Percentage HUNDRED = Percentage.of(HUNDRED_BIG_DECIMAL);

    private final BigDecimal value;

    private Percentage(BigDecimal value) {
        requireNonNull(value, "value must not be null");

        isTrue(value.compareTo(ZERO_BIG_DECIMAL) >= 0 && value.compareTo(HUNDRED_BIG_DECIMAL) <= 0,
                "value must be in [0,100]");

        this.value = value.stripTrailingZeros();
    }

    public static Percentage of(BigDecimal value) {
        return new Percentage(value);
    }

    public static Percentage of(String value) {
        requireNonNull(value, "value must not be null");

        return of(new BigDecimal(value));
    }

    public BigDecimal value() {
        return this.value;
    }

    public BigDecimal fractionalValue() {
        return this.value.divide(HUNDRED_BIG_DECIMAL, DEFAULT_PROBABILITY_SCALE, DEFAULT_PROBABILITY_ROUNDING);
    }

    public boolean isGreaterThan(Percentage other) {
        return compareTo(other) > 0;
    }

    public boolean isLessThan(Percentage other) {
        return compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Percentage other) {
        return compareTo(other) <= 0;
    }

    public boolean isHundred() {
        return compareTo(HUNDRED) == 0;
    }

    public Percentage plus(Percentage other) {
        BigDecimal sum = this.value.add(other.value);
        if (sum.compareTo(HUNDRED_BIG_DECIMAL) > 0) {
            sum = HUNDRED_BIG_DECIMAL;
        }
        return of(sum);
    }

    public Percentage minus(Percentage other) {
        BigDecimal diff = this.value.subtract(other.value);
        if (diff.compareTo(ZERO_BIG_DECIMAL) < 0) {
            diff = ZERO_BIG_DECIMAL;
        }
        return of(diff);
    }

    public Percentage times(BigDecimal factor) {
        requireNonNull(factor, "factor must not be null");

        isTrue(factor.compareTo(ZERO_BIG_DECIMAL) >= 0, "factor must be ≥ 0");

        BigDecimal multiplied = value.multiply(factor);

        if (multiplied.compareTo(HUNDRED_BIG_DECIMAL) > 0) {
            multiplied = HUNDRED_BIG_DECIMAL;
        }

        return of(multiplied);
    }

    @Override
    public int compareTo(Percentage other) {
        return this.value.compareTo(other.value);
    }
}
