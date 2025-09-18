package com.example.jackpot.domain.model.id;

import com.example.jackpot.domain.exception.InvalidUserIdException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

import static com.example.jackpot.domain.common.DomainAssertions.isNotNull;
import static com.example.jackpot.domain.common.DomainAssertions.isTrue;

/**
 * An user identifier.
 */
@ToString
@EqualsAndHashCode
public final class UserId {
    private final UUID value;

    private UserId(UUID value) {
        this.value = value;
    }

    public static UserId of(String value) {
        isNotNull(value, () -> new InvalidUserIdException("value must not be null"));

        isTrue(!value.isBlank(), () -> new InvalidUserIdException("value must not be blank"));
        isTrue(value.length() == 36, () -> new InvalidUserIdException("value has to be 36 characters"));

        try {
            return new UserId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new InvalidUserIdException("Invalid UUID format: %s".formatted(value));
        }
    }

    public static UserId of(UUID value) {
        isTrue(value != null, () -> new InvalidUserIdException("value must not be null"));

        return new UserId(value);
    }

    public UUID value() {
        return value;
    }
}
