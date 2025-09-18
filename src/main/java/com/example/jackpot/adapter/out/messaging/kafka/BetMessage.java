package com.example.jackpot.adapter.out.messaging.kafka;


import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;

public record BetMessage(
        String betId,
        String userId,
        String jackpotId,
        String amount,
        String currency
) {
    public static BetMessage from(Bet b) {
        return new BetMessage(
                b.betId().value().toString(),
                b.userId().value().toString(),
                b.jackpotId().value().toString(),
                b.betAmount().amount().toPlainString(),
                b.betAmount().currency().getCurrencyCode()
        );
    }

    public Bet toDomain() {
        return new Bet(
                BetId.of(betId),
                UserId.of(userId),
                JackpotId.of(jackpotId),
                Money.of(amount, currency)
        );
    }
}
