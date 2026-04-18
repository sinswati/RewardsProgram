package com.infy.dto;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

public class CustomerRewardDto {
	private String customerId;
	private Map<YearMonth, BigDecimal> monthlyPoints;
	private BigDecimal totalPoints;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public Map<YearMonth, BigDecimal> getMonthlyPoints() {
		return monthlyPoints;
	}

	public void setMonthlyPoints(Map<YearMonth, BigDecimal> monthlyPoints) {
		this.monthlyPoints = monthlyPoints;
	}

	public BigDecimal getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(BigDecimal totalPoints) {
		this.totalPoints = totalPoints;
	}

}
