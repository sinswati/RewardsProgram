package com.infy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.infy.dto.CustomerRewardDto;
import com.infy.entity.Transaction;
import com.infy.repository.TransactionRepo;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {
	@Mock
	private TransactionRepo repository;
	private RewardServiceImpl rewardService;

	@BeforeEach
	void setUp() {
		repository = Mockito.mock(TransactionRepo.class);
		rewardService = new RewardServiceImpl(repository);
	}

	// --- calculatePoints tests ---

	@Test
	void testCalculatePointsAbove100() {
		BigDecimal points = rewardService.calculatePoints(BigDecimal.valueOf(120));
		assertEquals(BigDecimal.valueOf(90), points);
	}

	@Test
	void testCalculatePointsExactly100() {
		BigDecimal points = rewardService.calculatePoints(BigDecimal.valueOf(100));
		assertEquals(BigDecimal.valueOf(50), points);
	}

	@Test
	void testCalculatePointsExactly50() {
		BigDecimal points = rewardService.calculatePoints(BigDecimal.valueOf(50));
		assertEquals(BigDecimal.ZERO, points);
	}

	@Test
	void testCalculatePointsZeroAmount() {
		BigDecimal points = rewardService.calculatePoints(BigDecimal.ZERO);
		assertEquals(BigDecimal.ZERO, points);
	}

	@Test
	void testCalculatePointsNegativeAmountThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> rewardService.calculatePoints(BigDecimal.valueOf(-10)));
	}

	@Test
	void testCalculatePointsNullAmountThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> rewardService.calculatePoints(null));
	}

	// --- calculateRewards tests ---

	@Test
	void testCalculateRewardsWithinDateRange() {
		Transaction t1 = new Transaction(1L, "C1", BigDecimal.valueOf(120), LocalDate.of(2026, 1, 15));
		Transaction t2 = new Transaction(2L, "C1", BigDecimal.valueOf(75), LocalDate.of(2026, 2, 10));
		Transaction t3 = new Transaction(3L, "C2", BigDecimal.valueOf(200), LocalDate.of(2026, 1, 20));

		when(repository.findByDateBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 30)))
				.thenReturn(List.of(t1, t2, t3));

		List<CustomerRewardDto> rewards = rewardService.calculateRewards(LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 3, 30));

		assertEquals(2, rewards.size());

		CustomerRewardDto c1 = rewards.stream().filter(r -> r.getCustomerId().equals("C1")).findFirst().orElseThrow();

		assertEquals(BigDecimal.valueOf(70), c1.getMonthlyPoints().get(YearMonth.of(2026, 1)));
		assertEquals(BigDecimal.valueOf(25), c1.getMonthlyPoints().get(YearMonth.of(2026, 2)));
		assertEquals(BigDecimal.valueOf(95), c1.getTotalPoints());

		CustomerRewardDto c2 = rewards.stream().filter(r -> r.getCustomerId().equals("C2")).findFirst().orElseThrow();

		assertEquals(BigDecimal.valueOf(150), c2.getMonthlyPoints().get(YearMonth.of(2026, 1)));
		assertEquals(BigDecimal.valueOf(150), c2.getTotalPoints());

	}

	@Test
	void testCalculateRewardsWithNullDatesThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> rewardService.calculateRewards(null, LocalDate.now()));
		assertThrows(IllegalArgumentException.class, () -> rewardService.calculateRewards(LocalDate.now(), null));
	}

	@Test
	void testCalculateRewardsEmptyTransactions() {
		when(repository.findByDateBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());

		List<CustomerRewardDto> rewards = rewardService.calculateRewards(LocalDate.of(2026, 1, 1),
				LocalDate.of(2026, 3, 31));

		assertTrue(rewards.isEmpty());
	}

}
