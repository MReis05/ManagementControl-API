package com.reis.ManagementControl_API.Entities.DTO;

import java.math.BigDecimal;

public class DailyTotalDTO {

	private BigDecimal totalValue;
	private BigDecimal cashTotalValue;
	private BigDecimal cardTotalValue;
	private BigDecimal pixTotalValue;
	
	public DailyTotalDTO() {
	}

	public DailyTotalDTO(BigDecimal totalValue, BigDecimal cashTotalValue, BigDecimal cardTotalValue,
			BigDecimal pixTotalValue) {
		super();
		this.totalValue = totalValue;
		this.cashTotalValue = cashTotalValue;
		this.cardTotalValue = cardTotalValue;
		this.pixTotalValue = pixTotalValue;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getCashTotalValue() {
		return cashTotalValue;
	}

	public void setCashTotalValue(BigDecimal cashTotalValue) {
		this.cashTotalValue = cashTotalValue;
	}

	public BigDecimal getCardTotalValue() {
		return cardTotalValue;
	}

	public void setCardTotalValue(BigDecimal cardTotalValue) {
		this.cardTotalValue = cardTotalValue;
	}

	public BigDecimal getPixTotalValue() {
		return pixTotalValue;
	}

	public void setPixTotalValue(BigDecimal pixTotalValue) {
		this.pixTotalValue = pixTotalValue;
	}
}
