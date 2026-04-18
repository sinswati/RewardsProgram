package com.infy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import com.infy.dto.CustomerRewardDto;
import com.infy.service.RewardService;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RewardsController.class)
class RewardsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	private RewardService rewardsService;

	@Test
	void testDefaultThreeMonths() throws Exception {
		CustomerRewardDto dto = new CustomerRewardDto();
		dto.setCustomerId("C001");
		dto.setMonthlyPoints(Map.of(YearMonth.of(2026, 1), new BigDecimal("90")));
		dto.setTotalPoints(new BigDecimal("90"));

		when(rewardsService.calculateRewards(any(), any())).thenReturn(List.of(dto));

		mockMvc.perform(get("/api/rewards")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].customerId").value("C001")).andExpect(jsonPath("$[0].totalPoints").value(90));
	}

	@Test
	void testMissingStartDate() throws Exception {
		mockMvc.perform(get("/api/rewards").param("endDate", "2026-03-31")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Both startDate and endDate must be provided"));
	}
}