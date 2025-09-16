package com.example.jackpot.application.service;

import com.example.jackpot.application.in.BetProcessingService;
import com.example.jackpot.application.out.JackpotContributionRepository;
import com.example.jackpot.application.out.JackpotRepository;
import com.example.jackpot.application.out.JackpotRewardRepository;
import com.example.jackpot.domain.exception.JackpotNotFoundException;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.Jackpot;
import com.example.jackpot.domain.model.JackpotContribution;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.JackpotId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultBetProcessingService implements BetProcessingService {

    private final JackpotRepository jackpotRepository;
    private final JackpotContributionRepository contributionRepository;
    private final JackpotRewardRepository rewardRepository;

    @Transactional
    @Override
    public void process(Bet bet) {
        requireNonNull(bet, "bet must not be null");

        if (contributionRepository.existsByBetId(bet.betId())) {
            log.warn("Contribution already exists for bet={}", bet.betId());
            return;
        }

        log.debug("Processing bet={} for jackpot={}", bet.betId(), bet.jackpotId());

        JackpotId jackpotId = bet.jackpotId();

        Jackpot jackpot = jackpotRepository.findById(jackpotId)
                .orElseThrow(() -> new JackpotNotFoundException("Jackpot not found: %s".formatted(jackpotId.value())));

        JackpotContribution contribution = jackpot.contribute(bet);
        contributionRepository.save(contribution);

        Optional<JackpotReward> reward = jackpot.evaluateRewardFor(bet);

        reward.ifPresent(r -> {
            rewardRepository.save(r);
            log.info("Reward granted for bet={} jackpot={} user={}", r.betId(), r.jackpotId(), r.userId());
        });

        jackpotRepository.save(jackpot);
    }
}
