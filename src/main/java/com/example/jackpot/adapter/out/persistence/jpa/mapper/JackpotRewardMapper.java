package com.example.jackpot.adapter.out.persistence.jpa.mapper;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotRewardEntity;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.CycleNumber;
import com.example.jackpot.domain.model.vo.JackpotCycle;

public final class JackpotRewardMapper {
    private JackpotRewardMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
    }

    public static JackpotReward toDomain(JackpotRewardEntity entity) {
        return new JackpotReward(
                BetId.of(entity.getBetId()),
                UserId.of(entity.getUserId()),
                JackpotCycle.of(
                        JackpotId.of(entity.getJackpotId()),
                        CycleNumber.of(entity.getJackpotCycle())
                ),
                MoneyMapper.toDomain(entity.getReward()),
                entity.getCreatedAt()
        );
    }

    public static JackpotRewardEntity toEntity(JackpotReward reward) {
        return new JackpotRewardEntity(
                reward.betId().value(),
                reward.userId().value(),
                reward.jackpotId().value(),
                reward.jackpotCycle().value(),
                MoneyMapper.toEmbeddable(reward.rewardAmount()),
                reward.createdAt()
        );
    }
}
