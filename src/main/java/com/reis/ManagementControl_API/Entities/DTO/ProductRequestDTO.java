package com.reis.ManagementControl_API.Entities.DTO;

import com.reis.ManagementControl_API.Entities.Enums.Category;

import jakarta.validation.constraints.NotBlank;

public class ProductRequestDTO {

	@NotBlank
	private String name;
	private Category Category;
	
	public ProductRequestDTO() {
	}
	
	public ProductRequestDTO(String name, Category category) {
		super();
		this.name = name;
		this.Category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return Category;
	}

	public void setCategory(Category category) {
		Category = category;
	}
}
