package com.example.jackpot.adapter.out.persistence.jpa.mapper;

import com.example.jackpot.adapter.out.persistence.jpa.config.contribution.ContributionConfigMapper;
import com.example.jackpot.adapter.out.persistence.jpa.config.reward.RewardConfigMapper;
import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotEntity;
import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.id.JackpotId;

public final class JackpotMapper {
    private JackpotMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass().getSimpleName()));
    }

    public static Jackpot toDomain(JackpotEntity entity) {
        return new Jackpot(
                JackpotId.of(entity.getId()),
                MoneyMapper.toDomain(entity.getInitial()),
                MoneyMapper.toDomain(entity.getCurrent()),
                ContributionConfigMapper.toDomain(entity.getContributionConfigJson()),
                RewardConfigMapper.toDomain(entity.getRewardConfigJson())
        );
    }

    public static JackpotEntity toEntity(Jackpot jackpot, Long version) {
        return new JackpotEntity(
                jackpot.jackpotId().value(),
                MoneyMapper.toEmbeddable(jackpot.initialPool()),
                MoneyMapper.toEmbeddable(jackpot.currentPool()),
                version
        );
    }
}
