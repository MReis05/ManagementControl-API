package com.reis.ManagementControl_API.Entities.DTO;

import java.math.BigDecimal;

public class TotalPerLocationDTO {

	private String locationName;
	private BigDecimal totalValue;
	
	public TotalPerLocationDTO() {
	}
	
	public TotalPerLocationDTO(String locationName, BigDecimal totalValue) {
		super();
		this.locationName = locationName;
		this.totalValue = totalValue;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}
}
