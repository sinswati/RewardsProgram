package com.infy.controller;

import com.infy.dto.CustomerRewardDto;
import com.infy.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardsController.class)
class RewardControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RewardService rewardService;

	@Test
	void testGetRewardsWithValidDateRange() throws Exception {
		CustomerRewardDto dto = new CustomerRewardDto();
		dto.setCustomerId("C1");
		dto.setMonthlyPoints(Map.of(YearMonth.of(2026, 1), BigDecimal.valueOf(90)));
		dto.setTotalPoints(BigDecimal.valueOf(90));

		when(rewardService.calculateRewards(eq(LocalDate.of(2026, 1, 1)), eq(LocalDate.of(2026, 3, 31))))
				.thenReturn(List.of(dto));

		mockMvc.perform(get("/api/rewards/calculate").param("startDate", "2026-01-01").param("endDate", "2026-03-31")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].customerId").value("C1")).andExpect(jsonPath("$[0].totalPoints").value(90));
	}

	@Test
	void testGetRewardsWithDefaultMonthsWhenDatesMissing() throws Exception {
		CustomerRewardDto dto = new CustomerRewardDto();
		dto.setCustomerId("C2");
		dto.setMonthlyPoints(Map.of(YearMonth.now(), BigDecimal.valueOf(50)));
		dto.setTotalPoints(BigDecimal.valueOf(50));

		when(rewardService.calculateRewards(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/rewards/calculate").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].customerId").value("C2")).andExpect(jsonPath("$[0].totalPoints").value(50));
	}

	@Test
	void testGetRewardsWithInvalidDateRange() throws Exception {
		mockMvc.perform(
				get("/api/rewards/calculate").param("startDate", "2026-01-01").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(content().string("[]"));
	}

	@Test
	void testGetRewardsNoResultsFound() throws Exception {
		when(rewardService.calculateRewards(any(LocalDate.class), any(LocalDate.class)))
				.thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/rewards/calculate").param("startDate", "2026-01-01").param("endDate", "2026-03-31")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}