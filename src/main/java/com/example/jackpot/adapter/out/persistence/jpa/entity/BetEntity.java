package com.example.jackpot.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "bet")
public class BetEntity implements Persistable<UUID> {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "jackpot_id", nullable = false)
    private UUID jackpotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jackpot_id", insertable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_bet_jackpot"))
    private JackpotEntity jackpot;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "bet_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "bet_currency", length = 3, nullable = false))
    private MoneyEmbeddable bet;


    protected BetEntity() {
        // Only for JPA
    }

    public BetEntity(UUID id, UUID userId, UUID jackpotId, MoneyEmbeddable bet) {
        this.id = id;
        this.userId = userId;
        this.jackpotId = jackpotId;
        this.bet = bet;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getJackpotId() {
        return jackpotId;
    }

    public MoneyEmbeddable getBet() {
        return bet;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
