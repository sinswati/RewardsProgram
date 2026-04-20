package com.infy.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.infy.dto.CustomerRewardDto;
import com.infy.entity.Transaction;
import com.infy.repository.TransactionRepo;

@Service
public class RewardServiceImpl implements RewardService {

	private static final Logger logger = LoggerFactory.getLogger(RewardServiceImpl.class);
	private final TransactionRepo repository;

	public RewardServiceImpl(TransactionRepo repository) {
		this.repository = repository;
	}

	public BigDecimal calculatePoints(BigDecimal amount) {

		logger.debug("Calculating points for amount: {}", amount);
		if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
			logger.error("Invalid transaction amount: {}", amount);
			throw new IllegalArgumentException("Amount must be non-null and positive");
		}
		BigDecimal points = BigDecimal.ZERO;
		if (amount.compareTo(BigDecimal.valueOf(100)) > 0) {
			points = points.add(amount.subtract(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(2)));
			points = points.add(BigDecimal.valueOf(50));
		} else if (amount.compareTo(BigDecimal.valueOf(50)) > 0) {
			points = points.add(amount.subtract(BigDecimal.valueOf(50)));
		}
		logger.info("Points calculated: {} for amount {}", points, amount);
		return points;
	}

	public List<CustomerRewardDto> calculateRewards(LocalDate startDate, LocalDate endDate) {

		if (startDate == null || endDate == null) {
			logger.error("Invalid date range: startDate={}, endDate={}", startDate, endDate);
			throw new IllegalArgumentException("Start and end dates must be provided");
		}

		List<Transaction> transactions = repository.findByDateBetween(startDate, endDate);

		if (transactions == null) {
			return Collections.emptyList();
		}

		Map<String, List<Transaction>> groupedByCustomer = transactions.stream()
				.collect(Collectors.groupingBy(Transaction::getCustomerId));

		List<CustomerRewardDto> rewardsList = new ArrayList<>();

		for (String customerId : groupedByCustomer.keySet()) {
			Map<YearMonth, BigDecimal> monthlyPoints = new HashMap<>();
			BigDecimal totalPoints = BigDecimal.ZERO;

			for (Transaction t : groupedByCustomer.get(customerId)) {
				BigDecimal points = calculatePoints(t.getAmount());
				YearMonth ym = YearMonth.from(t.getDate());
				monthlyPoints.put(ym, monthlyPoints.getOrDefault(ym, BigDecimal.ZERO).add(points));
				totalPoints = totalPoints.add(points);
			}

			CustomerRewardDto dto = new CustomerRewardDto();
			dto.setCustomerId(customerId);
			dto.setMonthlyPoints(monthlyPoints);
			dto.setTotalPoints(totalPoints);
			rewardsList.add(dto);
		}

		return rewardsList;
	}

}
