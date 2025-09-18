package com.example.jackpot.adapter.in.rest.dto.mapper;

import com.example.jackpot.adapter.in.rest.dto.BetRequest;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;

public final class BetMapper {
    private BetMapper() {
    }

    public static Bet toDomain(BetRequest r) {
        return new Bet(
                BetId.of(r.betId()),
                UserId.of(r.userId()),
                JackpotId.of(r.jackpotId()),
                MoneyMapper.toDomain(r.betAmount())
        );
    }
}
