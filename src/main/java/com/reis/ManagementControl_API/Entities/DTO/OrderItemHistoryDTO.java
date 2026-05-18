package com.reis.ManagementControl_API.Entities.DTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.reis.ManagementControl_API.Entities.Enums.Category;

public class OrderItemHistoryDTO {

	private String name;
	private BigDecimal totalValue;
	private BigDecimal averageUnitValue;
	private Category category;
	
	public OrderItemHistoryDTO() {
	}

	public OrderItemHistoryDTO(String name, BigDecimal totalValue, Double averageUnitValue, Category category) {
		super();
		this.name = name;
		this.totalValue = totalValue;
		this.averageUnitValue = BigDecimal.valueOf(averageUnitValue).setScale(2, RoundingMode.HALF_EVEN);
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getaverageUnitValue() {
		return averageUnitValue;
	}

	public void setaverageUnitValue(BigDecimal averageUnitValue) {
		this.averageUnitValue = averageUnitValue;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
