package com.example.jackpot.domain.common;

import java.util.function.Supplier;

/**
 * Utility class to perform domain-specific assertions.
 */
public final class DomainAssertions {

    private DomainAssertions() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean condition, Supplier<? extends RuntimeException> exSupplier) {
        if (!condition) {
            throw exSupplier.get();
        }
    }

    public static void isNotNull(Object obj, Supplier<? extends RuntimeException> exSupplier) {
        if (obj == null) {
            throw exSupplier.get();
        }
    }
}