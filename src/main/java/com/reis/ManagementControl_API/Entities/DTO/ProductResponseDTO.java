package com.reis.ManagementControl_API.Entities.DTO;

import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.Enums.Category;

public class ProductResponseDTO {

	private Long id;
	private String name;
	private Category category;
	
	public ProductResponseDTO() {
	}
	
	public ProductResponseDTO(Product obj) {
		this.id = obj.getId();
		this.name = obj.getName();
		this.category = obj.getCategory();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Category getCategory() {
		return category;
	}
}
