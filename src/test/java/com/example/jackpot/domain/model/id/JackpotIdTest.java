package com.example.jackpot.domain.model.id;

import com.example.jackpot.domain.exception.InvalidJackpotIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JackpotIdTest {

    @Test
    void givenNullString_whenOf_thenThrowException() {
        assertThatThrownBy(() -> JackpotId.of((String) null))
                .isInstanceOf(InvalidJackpotIdException.class)
                .hasMessage("value must not be null");
    }

    @ParameterizedTest(name = "[{index}] given blank input: \"{0}\"")
    @ValueSource(strings = {"", " ", "   ", "\t", "\n"})
    void givenBlankString_whenOf_thenThrowsIllegalArgument(String value) {
        assertThatThrownBy(() -> JackpotId.of(value))
                .isInstanceOf(InvalidJackpotIdException.class)
                .hasMessage("value must not be blank");
    }

    @ParameterizedTest(name = "[{index}] given invalid value length: \"{0}\"")
    @ValueSource(strings = {
            "123e4567-e89b-12d3-a456-42661417400",
            "123e4567-e89b-12d3-a456-4266141740012"
    })
    void givenInvalidValueLength_whenOf_thenThrowException(String value) {
        assertThatThrownBy(() -> JackpotId.of(value))
                .isInstanceOf(InvalidJackpotIdException.class)
                .hasMessage("value has to be 36 characters");
    }

    @Test
    void givenValueNotUuid_whenOf_thenThrowException() {
        String invalidUuid = "123e4567-e89b-12d3-a45689-not-a-uuid";

        assertThatThrownBy(() -> JackpotId.of(invalidUuid))
                .isInstanceOf(InvalidJackpotIdException.class)
                .hasMessage("Invalid UUID format: %s", invalidUuid);
    }

    @Test
    void givenValidString_whenOf_thenIdCreated() {
        UUID uuid = UUID.randomUUID();

        JackpotId result = JackpotId.of(uuid.toString());

        assertThat(result.value()).isEqualTo(uuid);
    }

    @Test
    void givenNullUuid_whenOf_thenThrowException() {
        assertThatThrownBy(() -> JackpotId.of((UUID) null))
                .isInstanceOf(InvalidJackpotIdException.class)
                .hasMessage("value must not be null");
    }

    @Test
    void givenValidUuid_whenOf_thenIdCreated() {
        UUID raw = UUID.randomUUID();

        JackpotId result = JackpotId.of(raw);

        assertThat(result.value()).isEqualTo(raw);
    }

}