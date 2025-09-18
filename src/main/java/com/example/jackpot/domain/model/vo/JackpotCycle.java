package com.example.jackpot.domain.model.vo;

import com.example.jackpot.domain.model.id.JackpotId;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import static java.util.Objects.requireNonNull;

/**
 * Represents a specific jackpot cycle, composed of a {@link JackpotId} and a {@link CycleNumber}.
 * <p>
 * Used to tag contributions and rewards with the exact jackpot and cycle.
 */
@EqualsAndHashCode
@ToString
public final class JackpotCycle {

    private final JackpotId jackpotId;
    private final CycleNumber cycle;

    private JackpotCycle(JackpotId jackpotId, CycleNumber cycle) {
        requireNonNull(jackpotId, "jackpotId must not be null");
        requireNonNull(cycle, "cycle must not be null");

        this.jackpotId = jackpotId;
        this.cycle = cycle;
    }

    public static JackpotCycle of(JackpotId id, CycleNumber cycle) {
        return new JackpotCycle(id, cycle);
    }

    public JackpotId jackpotId() {
        return jackpotId;
    }

    public CycleNumber cycle() {
        return cycle;
    }
}
