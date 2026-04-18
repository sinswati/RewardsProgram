package com.infy.controller;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.infy.dto.CustomerRewardDto;
import com.infy.exception.InvalidDateRangeException;
import com.infy.service.RewardService;

@RestController
@RequestMapping("/api/rewards")

public class RewardsController {

	private final RewardService rewardsService;

	private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);

	public RewardsController(RewardService rewardsService) {
		this.rewardsService = rewardsService;
	}

	@GetMapping("/calculate")
	public ResponseEntity<List<CustomerRewardDto>> getRewards(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false, defaultValue = "3") int months) {

		logger.info("Received request for rewards with startDate={}, endDate={}, months={}", startDate, endDate,
				months);

		if (startDate == null && endDate == null) {
			endDate = LocalDate.now();
			startDate = endDate.minusMonths(months);
		} else if (startDate == null || endDate == null) {
			throw new InvalidDateRangeException("Both startDate and endDate must be provided");
		}

		List<CustomerRewardDto> rewards = rewardsService.calculateRewards(startDate, endDate);
		if (rewards.isEmpty()) {
			logger.info("No rewards found for range {} to {}", startDate, endDate);
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(rewards);
	}

}
