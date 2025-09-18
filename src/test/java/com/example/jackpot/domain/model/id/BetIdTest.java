package com.example.jackpot.domain.model.id;

import com.example.jackpot.domain.exception.InvalidBetIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BetIdTest {

    @Test
    void givenNullString_whenOf_thenThrowException() {
        assertThatThrownBy(() -> BetId.of((String) null))
                .isInstanceOf(InvalidBetIdException.class)
                .hasMessage("value must not be null");
    }

    @ParameterizedTest(name = "[{index}] given blank input: \"{0}\"")
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void givenBlankString_whenOf_thenThrowsIllegalArgument(String value) {
        assertThatThrownBy(() -> BetId.of(value))
                .isInstanceOf(InvalidBetIdException.class)
                .hasMessage("value must not be blank");
    }

    @ParameterizedTest(name = "[{index}] given invalid value length: \"{0}\"")
    @ValueSource(strings = {
            "123e4567-e89b-12d3-a456-42661417400",
            "123e4567-e89b-12d3-a456-4266141740012",
            "732ade44-0807-45cd-87e1-60d04788d6fb"
    })
    void givenInvalidValueLength_whenOf_thenThrowException(String value) {
        assertThatThrownBy(() -> BetId.of(value))
                .isInstanceOf(InvalidBetIdException.class)
                .hasMessage("value has to be 36 characters");
    }

    @Test
    void givenValueNotUuid_whenOf_thenThrowException() {
        String invalidUuid = "123e4567-e89b-12d3-a45689-not-a-uuid";

        assertThatThrownBy(() -> BetId.of(invalidUuid))
                .isInstanceOf(InvalidBetIdException.class)
                .hasMessage("Invalid UUID format: %s", invalidUuid);
    }

    @Test
    void givenValidString_whenOf_thenIdCreated() {
        UUID uuid = UUID.randomUUID();

        BetId result = BetId.of(uuid.toString());

        assertThat(result.value()).isEqualTo(uuid);
    }

    @Test
    void givenNullUuid_whenOf_thenThrowException() {
        assertThatThrownBy(() -> BetId.of((UUID) null))
                .isInstanceOf(InvalidBetIdException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void givenUuid_whenOf_thenIdCreated() {
        UUID raw = UUID.randomUUID();

        BetId result = BetId.of(raw);

        assertThat(result.value()).isEqualTo(raw);
    }
}