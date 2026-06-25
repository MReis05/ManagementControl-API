package com.reis.ManagementControl_API.Entities.DTO;

import com.reis.ManagementControl_API.Entities.Location;

public class LocationResponseDTO {

	private Long id;
	private String name;
	
	public LocationResponseDTO() {
	}
	
	public LocationResponseDTO(Location obj) {
		this.id = obj.getId();
		this.name = obj.getName();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
