package com.example.jackpot.adapter.in.rest.dto;

public record RewardResponse(boolean won, MoneyDto reward) {

    public static RewardResponse noWin() {
        return new RewardResponse(false, null);
    }

    public static RewardResponse win(MoneyDto m) {
        return new RewardResponse(true, m);
    }
}