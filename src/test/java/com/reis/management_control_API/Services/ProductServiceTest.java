package com.reis.management_control_API.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Entities.Enums.Category;
import com.reis.ManagementControl_API.Repositories.ProductRepository;
import com.reis.ManagementControl_API.Services.ProductService;
import com.reis.ManagementControl_API.Services.Exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

	@Mock
	private ProductRepository repository;
	
	@InjectMocks
	private ProductService service;
	
	@Test
	@DisplayName("Should return a List<ProductResponseDTO> when find objects")
	void findAllSucessCase() {
		Product product = createStandardProduct();
		List<Product> listExpected = List.of(product);
		
		when(repository.findAll()).thenReturn(listExpected);
		
		List<ProductResponseDTO> listReceived = service.findAll();
		
		assertNotNull(listReceived);
		
		assertEquals(1, listReceived.size());
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).getId());
		assertEquals(listExpected.get(0).getName(), listReceived.get(0).getName());
		assertEquals(listExpected.get(0).getCategory(), listReceived.get(0).getCategory());
		
		verify(repository).findAll();
	}
	
	@Test
	@DisplayName("Should return a List<ProductReponseDTO> when find object with right name")
	void findByNameSuccessCase() {
		Product product = createStandardProduct();
		List<Product> listExpected = List.of(product);
		
		when(repository.findByNameContainingIgnoreCase("Coca Lata")).thenReturn(listExpected);
		
		List<ProductResponseDTO> listReceived = service.findByName("Coca Lata");
		
		assertNotNull(listReceived);
		
		assertEquals(1, listReceived.size());
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).getId());
		assertEquals(listExpected.get(0).getName(), listReceived.get(0).getName());
		assertEquals(listExpected.get(0).getCategory(), listReceived.get(0).getCategory());
		
		verify(repository).findByNameContainingIgnoreCase(any());
	}
	
	@Test
	@DisplayName("Should return a ProductResponseDTO when find object")
	void findByIdSuccessCase() {
		Long id = 1L;
		
		Product productExpected = createStandardProduct();
		
		when(repository.findById(id)).thenReturn(Optional.of(productExpected));
		
		ProductResponseDTO productReceived = service.findById(id);
		
		assertNotNull(productReceived);
		assertEquals(productExpected.getId(), productReceived.getId());
		assertEquals(productExpected.getName(), productReceived.getName());
		assertEquals(productExpected.getCategory(), productReceived.getCategory());
		
		verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void findByIdResouceNotFoundCase() {
		Long id = 99L;
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->{
			service.findById(id);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(id);
	}
	
	private Product createStandardProduct() {
		Product product = new Product("Coca Lata", Category.BEBIDAS);
		ReflectionTestUtils.setField(product, "id", 1L);
		return product;
	}
}
