package com.example.jackpot.application.service;

import com.example.jackpot.application.in.RewardQueryService;
import com.example.jackpot.application.out.JackpotRewardRepository;
import com.example.jackpot.domain.model.JackpotReward;
import com.example.jackpot.domain.model.id.BetId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DefaultRewardQueryServiceTest {

    @Mock
    private JackpotRewardRepository rewardRepository;

    private RewardQueryService service;

    @BeforeEach
    void setUp() {
        service = new DefaultRewardQueryService(rewardRepository);
    }

    @Test
    void givenNullBetId_whenFindById_thenThrowException() {
        assertThatThrownBy(() -> service.findByBetId(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("betId must not be null");
    }

    @Test
    void givenRewardDoesNotExist_whenFindById_thenReturnEmptyResult() {
        BetId betId = BetId.of(UUID.randomUUID());

        given(rewardRepository.findByBetId(betId)).willReturn(Optional.empty());

        Optional<JackpotReward> result = service.findByBetId(betId);

        assertThat(result).isEmpty();
    }

    @Test
    void givenRewardExists_whenFindById_thenReturnResult() {
        BetId betId = BetId.of(UUID.randomUUID());
        JackpotReward reward = mock(JackpotReward.class);

        given(rewardRepository.findByBetId(betId)).willReturn(Optional.of(reward));

        Optional<JackpotReward> result = service.findByBetId(betId);

        assertThat(result)
                .isNotEmpty()
                .hasValue(reward);
    }
}