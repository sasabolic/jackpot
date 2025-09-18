package com.example.jackpot.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;

/**
 * Represents a cycle number of a jackpot.
 */
@EqualsAndHashCode
@ToString
public final class CycleNumber {

    private final int value;

    private CycleNumber(int value) {
        isTrue(value >= 1, "Cycle number must be >= 1");

        this.value = value;
    }

    public static CycleNumber of(int v) { return new CycleNumber(v); }

    public CycleNumber next() { return new CycleNumber(value + 1); }

    public int value() {
        return value;
    }
}
