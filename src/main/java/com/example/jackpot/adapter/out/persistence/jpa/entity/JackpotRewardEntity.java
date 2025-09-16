package com.example.jackpot.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jackpot_reward",
        uniqueConstraints = @UniqueConstraint(name = "uq_reward_bet_user_jackpot",
                columnNames = {"bet_id","user_id","jackpot_id"})
)
public class JackpotRewardEntity implements Persistable<UUID> {
    @Id
    @Column(name = "bet_id", nullable = false)
    private UUID betId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "jackpot_id", nullable = false)
    private UUID jackpotId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns(
            value = {
                    @JoinColumn(name = "bet_id",     referencedColumnName = "id",        insertable = false, updatable = false),
                    @JoinColumn(name = "user_id",    referencedColumnName = "user_id",   insertable = false, updatable = false),
                    @JoinColumn(name = "jackpot_id", referencedColumnName = "jackpot_id",insertable = false, updatable = false)
            },
            foreignKey = @ForeignKey(name = "fk_reward_bet_user_jackpot")
    )
    private BetEntity bet;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "reward_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "reward_currency", length = 3, nullable = false))
    private MoneyEmbeddable reward;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected JackpotRewardEntity() {
        // Only for JPA
    }

    public JackpotRewardEntity(UUID betId, UUID userId, UUID jackpotId, MoneyEmbeddable reward, Instant createdAt) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.reward = reward;
        this.createdAt = createdAt;
    }

    public UUID getBetId() {
        return betId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getJackpotId() {
        return jackpotId;
    }

    public MoneyEmbeddable getReward() {
        return reward;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public UUID getId() {
        return getBetId();
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
