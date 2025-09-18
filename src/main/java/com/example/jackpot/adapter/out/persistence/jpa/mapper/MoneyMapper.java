package com.example.jackpot.adapter.out.persistence.jpa.mapper;

import com.example.jackpot.adapter.out.persistence.jpa.entity.MoneyEmbeddable;
import com.example.jackpot.domain.model.vo.Money;

import java.util.Currency;

public final class MoneyMapper {

    private MoneyMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
    }

    public static Money toDomain(MoneyEmbeddable e) {
        return Money.of(e.getAmount(), Currency.getInstance(e.getCurrency()));
    }

    public static MoneyEmbeddable toEmbeddable(Money m) {
        return new MoneyEmbeddable(m.amount(), m.currency().getCurrencyCode());
    }
}
