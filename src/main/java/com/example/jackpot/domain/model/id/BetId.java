package com.example.jackpot.domain.model.id;

import com.example.jackpot.domain.exception.InvalidBetIdException;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.example.jackpot.domain.common.DomainAssertions.isNotNull;
import static com.example.jackpot.domain.common.DomainAssertions.isTrue;

/**
 * A bet identifier.
 */
@Slf4j
@ToString
@EqualsAndHashCode
public final class BetId {
    private final UUID value;

    private BetId(UUID value) {
        this.value = value;
    }

    public static BetId of(String value) {
        isNotNull(value, () -> new InvalidBetIdException("value must not be null"));

        isTrue(!value.isBlank(), () -> new InvalidBetIdException("value must not be blank"));
        isTrue(value.length() == 36, () -> new InvalidBetIdException("value has to be 36 characters"));

        try {
            return new BetId(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            log.warn("Failed to parse value {}", value, ex);
            throw new InvalidBetIdException("Invalid UUID format: %s".formatted(value));
        }
    }

    public static BetId of(UUID value) {
        isTrue(value != null, () -> new InvalidBetIdException("value must not be null"));

        return new BetId(value);
    }

    public UUID value() {
        return value;
    }
}
