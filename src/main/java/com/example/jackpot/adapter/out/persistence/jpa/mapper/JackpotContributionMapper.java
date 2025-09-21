package com.example.jackpot.adapter.out.persistence.jpa.mapper;

import com.example.jackpot.adapter.out.persistence.jpa.entity.JackpotContributionEntity;
import com.example.jackpot.domain.model.JackpotContribution;

public final class JackpotContributionMapper {

    private JackpotContributionMapper() {
        throw new AssertionError("No instances of %s for you".formatted(this.getClass()));
    }

    public static JackpotContributionEntity toEntity(JackpotContribution contribution) {
        return new JackpotContributionEntity(
                contribution.betId().value(),
                contribution.userId().value(),
                contribution.jackpotId().value(),
                contribution.jackpotCycle().value(),
                MoneyMapper.toEmbeddable(contribution.stakeAmount()),
                MoneyMapper.toEmbeddable(contribution.contributionAmount()),
                MoneyMapper.toEmbeddable(contribution.currentJackpotAmount()),
                contribution.createdAt()
        );
    }
}
