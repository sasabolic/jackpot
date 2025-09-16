package com.example.jackpot.adapter.out.persistence.jpa.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "jackpot")
public class JackpotEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "initial_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false))
    private MoneyEmbeddable initial;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "current_amount", precision = 19, scale = 2, nullable = false))
    @AttributeOverride(name = "currency", column = @Column(name = "currency", length = 3, nullable = false, insertable = false, updatable = false))
    private MoneyEmbeddable current;

    @Lob
    @Column(name = "contribution_config_json", nullable = false, updatable = false)
    private String contributionConfigJson;

    @Lob
    @Column(name = "reward_config_json", nullable = false, updatable = false)
    private String rewardConfigJson;

    protected JackpotEntity() {
        // Only for JPA
    }

    public JackpotEntity(UUID id, MoneyEmbeddable initial, MoneyEmbeddable current) {
        this.id = id;
        this.initial = initial;
        this.current = current;
    }

    public JackpotEntity(UUID id, MoneyEmbeddable initial, MoneyEmbeddable current, String contributionConfigJson, String rewardConfigJson) {
        this.id = id;
        this.initial = initial;
        this.current = current;
        this.contributionConfigJson = contributionConfigJson;
        this.rewardConfigJson = rewardConfigJson;
    }

    public UUID getId() {
        return id;
    }

    public MoneyEmbeddable getInitial() {
        return initial;
    }

    public MoneyEmbeddable getCurrent() {
        return current;
    }

    public String getContributionConfigJson() {
        return contributionConfigJson;
    }

    public String getRewardConfigJson() {
        return rewardConfigJson;
    }
}

