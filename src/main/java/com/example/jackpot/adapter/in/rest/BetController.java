package com.example.jackpot.adapter.in.rest;

import com.example.jackpot.adapter.in.rest.dto.BetRequest;
import com.example.jackpot.adapter.in.rest.dto.RewardResponse;
import com.example.jackpot.adapter.in.rest.dto.mapper.BetMapper;
import com.example.jackpot.adapter.in.rest.dto.mapper.MoneyMapper;
import com.example.jackpot.application.port.in.PlaceBetService;
import com.example.jackpot.application.port.in.RewardQueryService;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/bets")
@RequiredArgsConstructor
public class BetController {

    private final PlaceBetService placeBetService;
    private final RewardQueryService rewardQueryService;

    @PostMapping
    public ResponseEntity<Void> place(@Valid @RequestBody BetRequest request) {
        placeBetService.place(BetMapper.toDomain(request));

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{betId}/reward")
    public ResponseEntity<RewardResponse> reward(@PathVariable String betId) {
        Optional<JackpotReward> reward = rewardQueryService.findByBetId(BetId.of(betId));

        return reward.map(r -> ResponseEntity.ok(RewardResponse.win(MoneyMapper.toDto(r.rewardAmount()))))
                .orElseGet(() -> ResponseEntity.ok(RewardResponse.noWin()));
    }
}
