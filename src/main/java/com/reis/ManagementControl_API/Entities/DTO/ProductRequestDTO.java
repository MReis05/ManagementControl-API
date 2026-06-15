package com.reis.ManagementControl_API.Entities.DTO;

import com.reis.ManagementControl_API.Entities.Enums.Category;

import jakarta.validation.constraints.NotBlank;

public class ProductRequestDTO {

	@NotBlank
	private String name;
	private Category category;
	
	public ProductRequestDTO() {
	}
	
	public ProductRequestDTO(String name, Category category) {
		super();
		this.name = name;
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
}
