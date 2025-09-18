package com.example.jackpot.adapter.in.rest.dto;

import com.example.jackpot.adapter.in.rest.dto.validation.ValidAmount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MoneyDto(
        @NotBlank
        @ValidAmount
        String amount,

        @NotNull
        @Pattern(regexp = "^[A-Z]{3}$", message = "currency must be a 3-letter ISO code")
        String currency
) {
}
