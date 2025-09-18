package com.example.jackpot.domain.model.vo;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import static com.example.jackpot.domain.common.DomainAssertions.isTrue;
import static java.util.Objects.requireNonNull;

@EqualsAndHashCode
@ToString
public final class Money implements Comparable<Money> {
    private static final int DEFAULT_SCALE = 2;
    private static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        requireNonNull(amount, "amount must not be null");
        requireNonNull(currency, "currency must not be null");

        this.currency = currency;
        this.amount = amount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currencyCode) {
        return of(new BigDecimal(amount), Currency.getInstance(currencyCode));
    }

    public Currency currency() {
        return this.currency;
    }

    public BigDecimal amount() {
        return this.amount;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean hasSameCurrencyAs(Money other) {
        return other != null && this.currency.equals(other.currency);
    }

    public boolean isGreaterThan(Money other) {
        requireSameCurrency(other);

        return compareTo(other) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        requireSameCurrency(other);

        return compareTo(other) >= 0;
    }

    public boolean isLessThan(Money other) {
        requireSameCurrency(other);

        return compareTo(other) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        requireSameCurrency(other);

        return compareTo(other) <= 0;
    }

    public Money plus(Money other) {
        requireSameCurrency(other);

        return of(this.amount.add(other.amount), this.currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);

        return of(this.amount.subtract(other.amount), this.currency);
    }

    public Money times(BigDecimal factor) {
        requireNonNull(factor, "factor must not be null");

        return of(this.amount.multiply(factor), this.currency);
    }

    public Money divide(BigDecimal divisor) {
        requireNonNull(divisor, "divisor must not be null");

        isTrue(divisor.compareTo(BigDecimal.ZERO) > 0, "divisor must be greater than zero");

        return of(this.amount.divide(divisor, DEFAULT_SCALE, DEFAULT_ROUNDING), currency);
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    private void requireSameCurrency(Money other) {
        requireNonNull(other, "other must not be null");

        isTrue(hasSameCurrencyAs(other), "Currencies must be equal [%s != %s]".formatted(this.currency, other.currency));
    }
}