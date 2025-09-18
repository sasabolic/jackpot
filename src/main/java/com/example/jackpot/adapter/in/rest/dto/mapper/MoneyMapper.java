package com.example.jackpot.adapter.in.rest.dto.mapper;

import com.example.jackpot.adapter.in.rest.dto.MoneyDto;
import com.example.jackpot.domain.model.vo.Money;

public final class MoneyMapper {
    private MoneyMapper() {
    }

    public static Money toDomain(MoneyDto dto) {
        return Money.of(dto.amount(), dto.currency());
    }

    public static MoneyDto toDto(Money m) {
        return new MoneyDto(m.amount().toPlainString(), m.currency().getCurrencyCode());
    }
}
