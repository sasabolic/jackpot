package com.example.jackpot.domain.model.id;

import com.example.jackpot.domain.exception.InvalidJackpotIdException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

import static com.example.jackpot.domain.common.DomainAssertions.isNotNull;
import static com.example.jackpot.domain.common.DomainAssertions.isTrue;

@ToString
@EqualsAndHashCode
public final class JackpotId {
    private final UUID value;

    private JackpotId(UUID value) {
        this.value = value;
    }

    public static JackpotId of(String value) {
        isNotNull(value, () -> new InvalidJackpotIdException("value must not be null"));

        isTrue(!value.isBlank(), () -> new InvalidJackpotIdException("value must not be blank"));
        isTrue(value.length() == 36, () -> new InvalidJackpotIdException("value has to be 36 characters"));

        try {
            return new JackpotId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new InvalidJackpotIdException("Invalid UUID format: %s".formatted(value));
        }
    }

    public static JackpotId of(UUID value) {
        isTrue(value != null, () -> new InvalidJackpotIdException("value must not be null"));

        return new JackpotId(value);
    }

    public UUID value() {
        return value;
    }
}
