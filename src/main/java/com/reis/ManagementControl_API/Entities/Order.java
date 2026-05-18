package com.reis.ManagementControl_API.Entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.reis.ManagementControl_API.Entities.Enums.PaymentMethod;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_orders")
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDate date;
	private BigDecimal totalValue;
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;
	
	@ManyToOne
	private Location location;
	
	@OneToMany(mappedBy = "id.order", fetch = FetchType.EAGER, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();
	
	public Order() {
	}

	public Order(LocalDate date, PaymentMethod paymentMethod) {
		super();
		this.date = date;
		this.paymentMethod = paymentMethod;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Long getId() {
		return id;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public BigDecimal getTotal() {
		BigDecimal totalValue = BigDecimal.ZERO;
		
		for (OrderItem i : items) {
			totalValue = totalValue.add(i.getTotalValue());
		}
		
		return totalValue;
	}
	
	public void updateTotal() {
		this.totalValue = getTotal();
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
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}
}
