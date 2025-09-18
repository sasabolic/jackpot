package com.example.jackpot.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

/**
 * Represents a positive decay factor used in rate decay calculations.
 */
@EqualsAndHashCode
@ToString
public final class DecayFactor {

    private final BigDecimal value;

    private DecayFactor(BigDecimal value) {
        requireNonNull(value, "value must not be null");

        isTrue(value.compareTo(BigDecimal.ZERO) > 0, "value must be > 0");

        this.value = value.stripTrailingZeros();
    }

    public static DecayFactor of(BigDecimal value) {
        return new DecayFactor(value);
    }

    public static DecayFactor of(String value) {
        requireNonNull(value, "value must not be null");

        return of(new BigDecimal(value));
    }

    public BigDecimal value() {
        return this.value;
    }
}