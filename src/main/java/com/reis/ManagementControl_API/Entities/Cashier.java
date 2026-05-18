package com.reis.ManagementControl_API.Entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cashier")
public class Cashier implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private BigDecimal currentCashier;
	private BigDecimal expectedTransfer;
	private BigDecimal currentCashierPlusTransfer;
	
	@OneToMany(mappedBy = "cashier", fetch = FetchType.EAGER, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	private List<Transaction> transactions = new ArrayList<>();
	
	public Cashier() {
	}

	public Cashier(Long id, BigDecimal currentCashier, BigDecimal expectedTransfer, BigDecimal currentCashierPlusTransfer) {
		super();
		this.id = id;
		this.currentCashier = currentCashier;
		this.expectedTransfer = expectedTransfer;
		this.currentCashierPlusTransfer = currentCashierPlusTransfer;
	}

	public BigDecimal getCurrentCashier() {
		return currentCashier;
	}

	public void setCurrentCashier(BigDecimal currentCashier) {
		this.currentCashier = currentCashier.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getExpectedTransfer() {
		return expectedTransfer;
	}

	public void setExpectedTransfer(BigDecimal expectedTransfer) {
		this.expectedTransfer = expectedTransfer.setScale(2, RoundingMode.HALF_EVEN);
	}

	public Long getId() {
		return id;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public BigDecimal getCurrentCashierPlusTransfer() {
		return currentCashierPlusTransfer;
	}
	
	public void updateTotal() {
		this.currentCashierPlusTransfer = currentCashier.add(expectedTransfer).setScale(2, RoundingMode.HALF_EVEN);
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
		Cashier other = (Cashier) obj;
		return Objects.equals(id, other.id);
	}
}
