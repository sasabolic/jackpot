package com.example.jackpot.adapter.in.rest;

import com.example.jackpot.adapter.in.rest.dto.BetRequest;
import com.example.jackpot.adapter.in.rest.dto.MoneyDto;
import com.example.jackpot.application.in.PlaceBetService;
import com.example.jackpot.application.in.RewardQueryService;
import com.example.jackpot.domain.model.Bet;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import com.example.jackpot.domain.model.vo.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BetController.class)
class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceBetService placeBetService;

    @MockitoBean
    private RewardQueryService rewardQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRequest_whenPlace_thenAccepted() throws Exception {
        BetRequest betRequest = new BetRequest(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                new MoneyDto("10.00", "EUR")
        );

        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(betRequest)))
                .andExpect(status().isAccepted());

        ArgumentCaptor<Bet> captor = ArgumentCaptor.forClass(Bet.class);
        then(placeBetService).should().place(captor.capture());

        assertThat(captor.getValue())
                .isNotNull()
                .satisfies(b -> {
                    assertThat(b.betId().value()).isEqualTo(UUID.fromString(betRequest.betId()));
                    assertThat(b.userId().value()).isEqualTo(UUID.fromString(betRequest.userId()));
                    assertThat(b.jackpotId().value()).isEqualTo(UUID.fromString(betRequest.jackpotId()));
                    assertThat(b.betAmount().amount()).hasToString(betRequest.betAmount().amount());
                    assertThat(b.betAmount().currency()).hasToString(betRequest.betAmount().currency());
                });
    }

    @Test
    void givenInvalidRequest_whenPlace_thenBadRequest() throws Exception {
        BetRequest betRequest = new BetRequest(
                "invalid-bet-id",
                "invalid-user-id",
                "invalid-jackpot-id",
                new MoneyDto("0.00", "EUR")
        );

        mockMvc.perform(post("/api/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(betRequest)))
                .andExpect(status().isBadRequest());

        then(placeBetService).should(never()).place(any());
    }

    @Test
    void givenNoReward_whenGetReward_thenNoWinResponse() throws Exception {
        String betId = UUID.randomUUID().toString();

        given(rewardQueryService.findByBetId(any(BetId.class))).willReturn(Optional.empty());

        mockMvc.perform(get("/api/bets/{betId}/reward", betId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.won").value(false))
                .andExpect(jsonPath("$.reward").value(nullValue()));
    }

    @Test
    void givenReward_whenGetReward_thenWinResponse() throws Exception {
        String betId = UUID.randomUUID().toString();
        Money rewardAmount = Money.of("10000.00", "EUR");

        JackpotReward reward = mock(JackpotReward.class);
        given(reward.rewardAmount()).willReturn(rewardAmount);

        given(rewardQueryService.findByBetId(any(BetId.class))).willReturn(Optional.of(reward));

        mockMvc.perform(get("/api/bets/{betId}/reward", betId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.won").value(true))
                .andExpect(jsonPath("$.reward.amount").value(rewardAmount.amount()))
                .andExpect(jsonPath("$.reward.currency").value(rewardAmount.currency().toString()));
    }
}