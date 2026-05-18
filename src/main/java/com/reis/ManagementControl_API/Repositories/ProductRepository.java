package com.reis.ManagementControl_API.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reis.ManagementControl_API.Entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByNameContainingIgnoreCase(String name);
	boolean existsByNameIgnoreCase(String name);
}
