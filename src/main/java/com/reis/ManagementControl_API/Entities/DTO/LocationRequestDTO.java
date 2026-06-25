package com.reis.ManagementControl_API.Entities.DTO;

import jakarta.validation.constraints.NotBlank;

public class LocationRequestDTO {

	@NotBlank
	private String name;
	
	public LocationRequestDTO () {
	}

	public LocationRequestDTO(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
