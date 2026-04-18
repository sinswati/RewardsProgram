package com.infy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infy.dto.CustomerRewardDto;
import com.infy.entity.Transaction;
import com.infy.repository.TransactionRepo;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    @Mock
    private TransactionRepo repository;

    @InjectMocks
    private RewardService rewardsService;

    @Test
    void testCalculatePointsBoundaryValues() {
        assertEquals(BigDecimal.ZERO, rewardsService.calculatePoints(new BigDecimal("0")));
        assertEquals(BigDecimal.ZERO, rewardsService.calculatePoints(new BigDecimal("49.99")));
        assertEquals(new BigDecimal("0"), rewardsService.calculatePoints(new BigDecimal("50.00")));
        assertEquals(new BigDecimal("50"), rewardsService.calculatePoints(new BigDecimal("100.00")));
        assertEquals(new BigDecimal("50.02"), rewardsService.calculatePoints(new BigDecimal("100.01")));
    }

    @Test
    void testCalculateRewardsGrouping() {
        List<Transaction> transactions = Arrays.asList(
            new Transaction(1L, "C001", new BigDecimal("120.00"), LocalDate.of(2025, 1, 15)),
            new Transaction(2L, "C001", new BigDecimal("120.00"), LocalDate.of(2026, 1, 15))
        );
        when(repository.findByDateBetween(any(), any())).thenReturn(transactions);

        List<CustomerRewardDto> rewards = rewardsService.calculateRewards(
            LocalDate.of(2025, 1, 1), LocalDate.of(2026, 12, 31)
        );

        assertEquals(1, rewards.size());
        assertTrue(rewards.get(0).getMonthlyPoints().containsKey(YearMonth.of(2025, 1)));
        assertTrue(rewards.get(0).getMonthlyPoints().containsKey(YearMonth.of(2026, 1)));
    }
}
