package com.example.jackpot.application.in;

import com.example.jackpot.domain.model.Bet;
import org.springframework.transaction.annotation.Transactional;

public interface BetProcessingService {
    @Transactional
    void process(Bet bet);
}
