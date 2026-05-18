package com.reis.ManagementControl_API.Entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.reis.ManagementControl_API.Entities.PK.OrderItemPK;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OrderItemPK id = new OrderItemPK();
	
	@Column(precision = 10, scale = 3)
	private BigDecimal quantity;
	
	@Column(precision = 10, scale = 3)
	private BigDecimal unitValue;
	
	public OrderItem() {
	}

	public OrderItem(BigDecimal quantity, BigDecimal unitValue, Order order, Product product) {
		super();
		id.setOrder(order);
		id.setProduct(product);
		this.quantity = quantity;
		this.unitValue = unitValue;
	}

	public Order getOrder() {
		return id.getOrder();
	}
	
	public void setOrder(Order order) {
		id.setOrder(order);
	}
	
	public Product getProduct() {
		return id.getProduct();
	}
	
	public void setProduct(Product product) {
		id.setProduct(product);
	}
	
	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity.setScale(3, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(BigDecimal unitValue) {
		this.unitValue = unitValue.setScale(2, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getTotalValue() {
		return unitValue.multiply(quantity).setScale(2, RoundingMode.HALF_EVEN);
	}
}
