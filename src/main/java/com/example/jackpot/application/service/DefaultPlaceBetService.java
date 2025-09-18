package com.example.jackpot.application.service;

import com.example.jackpot.application.port.in.PlaceBetService;
import com.example.jackpot.application.port.out.BetProducer;
import com.example.jackpot.application.port.out.BetRepository;
import com.example.jackpot.domain.model.Bet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultPlaceBetService implements PlaceBetService {

    private final BetRepository betRepository;
    private final BetProducer betProducer;

    @Transactional
    public void place(Bet bet) {
        requireNonNull(bet, "bet must not be null");

        if (betRepository.existsById(bet.betId())) {
            log.warn("Bet already exists, skipping publish (betId={})", bet.betId());
            return;
        }
        betRepository.save(bet);

        betProducer.publish(bet);

        log.info("Bet persisted and published bet={}", bet);
    }
}