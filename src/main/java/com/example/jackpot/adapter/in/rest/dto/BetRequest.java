package com.example.jackpot.adapter.in.rest.dto;

import com.example.jackpot.adapter.in.rest.dto.validation.ValidId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BetRequest(
        @NotBlank
        @ValidId
        String betId,

        @NotBlank
        @ValidId
        String userId,

        @NotBlank
        @ValidId
        String jackpotId,

        @NotNull
        @Valid
        MoneyDto betAmount
) {
}