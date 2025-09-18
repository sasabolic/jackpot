package com.example.jackpot.application.service;

import com.example.jackpot.application.in.PlaceBetService;
import com.example.jackpot.application.out.BetProducer;
import com.example.jackpot.application.out.BetRepository;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.id.JackpotId;
import com.example.jackpot.domain.model.id.UserId;
import com.example.jackpot.domain.model.vo.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DefaultPlaceBetServiceTest {

    @Mock
    private BetRepository betRepository;

    @Mock
    private BetProducer betProducer;

    private PlaceBetService service;

    @BeforeEach
    void setUp() {
        service = new DefaultPlaceBetService(betRepository, betProducer);
    }

    @Test
    void givenNullBet_whenPlace_thenThrowException() {
        assertThatThrownBy(() -> service.place(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("bet must not be null");
    }

    @Test
    void givenBetAlreadyExists_whenPlace_thenDoNothing() {
        BetId betId = BetId.of(UUID.randomUUID());

        Bet bet = new Bet(
                betId,
                UserId.of(UUID.randomUUID()),
                JackpotId.of(UUID.randomUUID()),
                Money.of("105.00", "EUR")
        );

        given(betRepository.existsById(betId)).willReturn(true);

        service.place(bet);

        then(betRepository).shouldHaveNoMoreInteractions();
        then(betProducer).shouldHaveNoInteractions();
    }

    @Test
    void whenPlace_thenContributedAndRewardEvaluated() {
        BetId betId = BetId.of(UUID.randomUUID());
        JackpotId jackpotId = JackpotId.of(UUID.randomUUID());

        Bet bet = new Bet(
                betId,
                UserId.of(UUID.randomUUID()),
                jackpotId,
                Money.of("105.00", "EUR")
        );

        given(betRepository.existsById(betId)).willReturn(false);

        service.place(bet);

        then(betRepository).should(times(1)).save(bet);
        then(betProducer).should(times(1)).publish(bet);

        then(betRepository).shouldHaveNoMoreInteractions();
        then(betProducer).shouldHaveNoMoreInteractions();
    }
}