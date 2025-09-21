package com.example.jackpot.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jackpot_contribution",
        uniqueConstraints = @UniqueConstraint(name = "uq_contribution_bet_user_jackpot",
                columnNames = {"bet_id", "user_id", "jackpot_id"})
)
public class JackpotContributionEntity implements Persistable<UUID> {
    @Id
    @Column(name = "bet_id", nullable = false)
    private UUID betId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "jackpot_id", nullable = false)
    private UUID jackpotId;

    @Column(name = "jackpot_cycle", nullable = false)
    private int jackpotCycle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns(
            value = {
                    @JoinColumn(name = "bet_id", referencedColumnName = "id", insertable = false, updatable = false),
                    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false),
                    @JoinColumn(name = "jackpot_id", referencedColumnName = "jackpot_id", insertable = false, updatable = false)
            },
            foreignKey = @ForeignKey(name = "fk_contribution_bet_user_jackpot")
    )
    private BetEntity bet;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "stake_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    private MoneyEmbeddable stake;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "contribution_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false, insertable = false, updatable = false))
    private MoneyEmbeddable contribution;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "current_jackpot_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false, insertable = false, updatable = false))
    private MoneyEmbeddable currentJackpot;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected JackpotContributionEntity() {
        // Only for JPA
    }

    public JackpotContributionEntity(UUID betId, UUID userId, UUID jackpotId, int jackpotCycle,
                                     MoneyEmbeddable stake, MoneyEmbeddable contribution,
                                     MoneyEmbeddable currentJackpot, Instant createdAt) {
        this.betId = betId;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.jackpotCycle = jackpotCycle;
        this.stake = stake;
        this.contribution = contribution;
        this.currentJackpot = currentJackpot;
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

    public int getJackpotCycle() {
        return jackpotCycle;
    }

    public MoneyEmbeddable getStake() {
        return stake;
    }

    public MoneyEmbeddable getContribution() {
        return contribution;
    }

    public MoneyEmbeddable getCurrentJackpot() {
        return currentJackpot;
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
