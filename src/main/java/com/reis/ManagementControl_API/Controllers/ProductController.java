package com.reis.ManagementControl_API.Controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.ManagementControl_API.Entities.DTO.ProductRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Services.ProductService;

import jakarta.validation.Valid;

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
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductResponseDTO> findById(@PathVariable Long id){
		ProductResponseDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<ProductResponseDTO> insert(@Valid @RequestBody ProductRequestDTO dto){
		ProductResponseDTO resp = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value ="/{id}")
	public ResponseEntity<ProductResponseDTO> update(@PathVariable Long id, @RequestBody ProductRequestDTO dto){
		ProductResponseDTO resp = service.update(id, dto);
		return ResponseEntity.ok().body(resp);
	}
	
	@DeleteMapping(value ="/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}	
}
