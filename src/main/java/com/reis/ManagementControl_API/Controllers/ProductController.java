package com.reis.ManagementControl_API.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Services.ProductService;

@RestController
@RequestMapping(value = "/products")
public class ProductController {

	@Autowired
	private ProductService service;
	
	@GetMapping
	public ResponseEntity<List<ProductResponseDTO>> findProducts(@RequestParam(required = false) String name){
		List<ProductResponseDTO> dto;
		if(name != null) {
			dto = service.findByName(name);
		}
		else {
			dto = service.findAll();
		}
		return ResponseEntity.ok().body(dto);
	}
}
