package com.infy.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.infy.dto.CustomerRewardDto;

@Service
public interface RewardService {

	List<CustomerRewardDto> calculateRewards(LocalDate startDate, LocalDate endDate);
	 public BigDecimal calculatePoints(BigDecimal amount);

}
