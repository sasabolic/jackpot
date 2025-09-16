package com.example.jackpot.adapter.out.persistence.jpa.mapper;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotRewardEntity;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;

public final class JackpotRewardMapper {
    private JackpotRewardMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
    }

    public static JackpotRewardEntity toEntity(JackpotReward reward) {
        return new JackpotRewardEntity(
                reward.betId().value(),
                reward.userId().value(),
                reward.jackpotId().value(),
                MoneyMapper.toEmbeddable(reward.rewardAmount()),
                reward.createdAt()
        );
    }

    public static JackpotReward toDomain(JackpotRewardEntity entity) {
        return new JackpotReward(
                BetId.of(entity.getBetId()),
                UserId.of(entity.getUserId()),
                JackpotId.of(entity.getJackpotId()),
                MoneyMapper.toDomain(entity.getReward()),
                entity.getCreatedAt()
        );
    }
}
