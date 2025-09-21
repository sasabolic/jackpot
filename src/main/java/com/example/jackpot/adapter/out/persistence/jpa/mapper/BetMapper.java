package com.example.jackpot.adapter.out.persistence.jpa.mapper;

import com.example.jackpot.adapter.out.persistence.jpa.entity.BetEntity;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;

public final class BetMapper {

    private BetMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass()));
    }

    public static Bet toDomain(BetEntity entity) {
        return new Bet(
                BetId.of(entity.getId()),
                UserId.of(entity.getUserId()),
                JackpotId.of(entity.getJackpotId()),
                MoneyMapper.toDomain(entity.getBet())
        );
    }

    public static BetEntity toEntity(Bet bet) {
        return new BetEntity(
                bet.betId().value(),
                bet.userId().value(),
                bet.jackpotId().value(),
                MoneyMapper.toEmbeddable(bet.betAmount())
        );
    }
}
