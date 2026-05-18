package com.reis.ManagementControl_API.Entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_transactions")
public class Transaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String source;
	private BigDecimal transactionValue;;
	private BigDecimal currentCashier;
	private BigDecimal newValue;
	private LocalDateTime transactionTime;
	
	@ManyToOne
	private Cashier cashier;
	
	public Transaction() {
	}

	public Transaction(String source, BigDecimal transactionValue, BigDecimal currentCashier, BigDecimal newValue, LocalDateTime transactionTime) {
		super();
		this.source = source;
		this.transactionValue = transactionValue;
		this.currentCashier = currentCashier;
		this.newValue = newValue;
		this.transactionTime = transactionTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public BigDecimal getTransactionValue() {
		return transactionValue;
	}

	public void setTransactionValue(BigDecimal transactionValue) {
		this.transactionValue = transactionValue.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getCurrentCashier() {
		return currentCashier;
	}

	public void setCurrentCashier(BigDecimal currentCashier) {
		this.currentCashier = currentCashier.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getNewValue() {
		return newValue;
	}

	public void setNewValue(BigDecimal newValue) {
		this.newValue = newValue.setScale(2, RoundingMode.HALF_EVEN);
	}

	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(LocalDateTime transactionTime) {
		this.transactionTime = transactionTime;
	}

	public Cashier getCashier() {
		return cashier;
	}

	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		return Objects.equals(id, other.id);
	}
}
