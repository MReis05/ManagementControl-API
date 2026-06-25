package com.reis.management_control_API.Services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.DTO.ProductRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Entities.Enums.Category;
import com.reis.ManagementControl_API.Repositories.ProductRepository;
import com.reis.ManagementControl_API.Services.ProductService;
import com.reis.ManagementControl_API.Services.Exceptions.DatabaseException;
import com.reis.ManagementControl_API.Services.Exceptions.DataConflitException;
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
	
	@Test
	@DisplayName("Should save product and return a ProductResponseDTO")
	void insertSuccessCase() {
		ProductRequestDTO dto = new ProductRequestDTO("Oleo", Category.INSUMOS_SECUNDARIOS);
		
		when(repository.save(any(Product.class))).thenAnswer(invocation ->{
			Product p = invocation.getArgument(0);
			ReflectionTestUtils.setField(p, "id", 1L);
			return p;
		});
		
		ProductResponseDTO productReceived = service.insert(dto);
		
		assertNotNull(productReceived);
		assertEquals(dto.getName(), productReceived.getName());
		assertEquals(dto.getCategory(), productReceived.getCategory());
		
		verify(repository).save(any(Product.class));
	}
	
	@Test
	@DisplayName("Should throw a DataConflitExeception when already exists a product with the name")
	void insertDataConflitCase() {
		ProductRequestDTO dto = new ProductRequestDTO("Oleo", Category.INSUMOS_SECUNDARIOS);
		
		when(repository.existsByNameIgnoreCase("Oleo")).thenReturn(true);
		
		DataConflitException exception = assertThrows(DataConflitException.class, () ->{
			service.insert(dto);
		});
		
		assertNotNull(exception);
		assertEquals(DataConflitException.class, exception.getClass());
		assertEquals("Esse objeto já existe em nosso banco de dados", exception.getMessage());
	}
	
	@Test
	@DisplayName("Should update only provided fields and keep others unchanged")
	void updateSuccessCase() {
		Product obj = createStandardProduct();
		
		when(repository.findById(1L)).thenReturn(Optional.of(obj));
		
		ProductRequestDTO dto = new ProductRequestDTO();
		dto.setName("Coca de 1 Litro");
		
		when(repository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		ProductResponseDTO productReceived = service.update(1L, dto);
		
		assertEquals(obj.getId(), productReceived.getId());
		assertEquals(obj.getCategory(), productReceived.getCategory());
		assertEquals(dto.getName(), productReceived.getName());
		
		verify(repository).findById(any());
		verify(repository).save(any());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void updateResourceNotFoundCase() {
		Long id = 99L;
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		ProductRequestDTO dto = new ProductRequestDTO();
		dto.setName("Coca de 1 Litro");
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->{
			service.update(id, dto);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(id);
		verify(repository, never()).save(any());
	}
	
	@Test
	@DisplayName("Should delete product when id exists and has no dependencies")
	void deleteSuccessCase() {
		Long id = 1L;
		
		when(repository.existsById(id)).thenReturn(true);
		
		service.delete(id);
		
		verify(repository).existsById(id);
		verify(repository).deleteById(id);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when id doesn't exist")
	void deleteResourceNotFoundCase() {
		Long id = 99L;
		
		when(repository.existsById(id)).thenReturn(false);
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(id);
		});
		
		assertNotNull(exception);
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository, never()).deleteById(id);
	}
	
	@Test
	@DisplayName("Should throw DatabaseException when deleting product with dependencies")
	void deleteDataIntegrityViolationCase() {
		Long id = 1L;
		
		when(repository.existsById(id)).thenReturn(true);
		
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(id);
		
		DatabaseException exception = assertThrows(DatabaseException.class, () ->{
			service.delete(1L);
		});
		
		assertNotNull(exception);
		assertEquals(DatabaseException.class, exception.getClass());
		verify(repository, times(1)).deleteById(id);
	}
	
	private Product createStandardProduct() {
		Product product = new Product("Coca Lata", Category.BEBIDAS);
		ReflectionTestUtils.setField(product, "id", 1L);
		return product;
	}
}
