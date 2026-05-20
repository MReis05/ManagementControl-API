package com.reis.ManagementControl_API.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.DTO.ProductRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Repositories.ProductRepository;
import com.reis.ManagementControl_API.Services.Exceptions.DatabaseException;
import com.reis.ManagementControl_API.Services.Exceptions.ProductExistsException;
import com.reis.ManagementControl_API.Services.Exceptions.ResourceNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)
	public List<ProductResponseDTO> findAll(){
		return repository.findAll().stream().map(ProductResponseDTO::new).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<ProductResponseDTO> findByName(String name){
		return repository.findByNameContainingIgnoreCase(name).stream().map(ProductResponseDTO::new).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public ProductResponseDTO findById(Long id) {
		Product product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		return new ProductResponseDTO(product);
	}
	
	@Transactional
	public ProductResponseDTO save (ProductRequestDTO dto) {
		if(repository.existsByNameIgnoreCase(dto.getName())) {
			throw new ProductExistsException();
		}
		Product product = new Product(dto.getName(), dto.getCategory());
		product = repository.save(product);
		return new ProductResponseDTO(product);
	}
	
	public boolean existsByNameIgnoreCase(String name) {
		return repository.existsByNameIgnoreCase(name);
	}
	
	@Transactional
	public ProductResponseDTO update(Long Id, ProductRequestDTO dto) {
		Product product = repository.findById(Id).orElseThrow(() -> new ResourceNotFoundException(Id));
		
		updateData(product, dto);
		
		product = repository.save(product);
		
		return new ProductResponseDTO(product);
	}
	
	@Transactional
	public void delete(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException(id);
		}
		
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	private void updateData(Product product, ProductRequestDTO dto) {
		if(dto.getName() != null) {
			product.setName(dto.getName());
		}
		if(dto.getCategory() != null) {
			product.setCategory(dto.getCategory());
		}
	}
}
