package com.reis.management_control_API.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reis.ManagementControl_API.ManagementControlApiApplication;
import com.reis.ManagementControl_API.Controllers.ProductController;
import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.DTO.ProductRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Entities.Enums.Category;
import com.reis.ManagementControl_API.Services.ProductService;
import com.reis.ManagementControl_API.Services.Exceptions.DatabaseException;
import com.reis.ManagementControl_API.Services.Exceptions.ProductExistsException;
import com.reis.ManagementControl_API.Services.Exceptions.ResourceNotFoundException;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = ManagementControlApiApplication.class)
public class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockitoBean
	private ProductService service;
	
	@Test
	@DisplayName("Should return 200 Ok and a List of Products")
	void findAllSucessCase() throws Exception {
		ProductResponseDTO dto = new ProductResponseDTO(createStandardProduct());
		
		when(service.findAll()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/products")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].name").value("Coca Lata"))
				.andExpect(jsonPath("$[0].category").value(Category.BEBIDAS.toString()));
		
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of Products with right name")
	void findByNameSuccessCase() throws Exception {
		ProductResponseDTO dto = new ProductResponseDTO(createStandardProduct());
		
		when(service.findByName("Coca Lata")).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/products?name=Coca Lata")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].name").value("Coca Lata"))
				.andExpect(jsonPath("$[0].category").value(Category.BEBIDAS.toString()));
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a ProductResponseDTO")
	void findByIdSuccessCase() throws Exception {
		Long id = 1L;
		
		when(service.findById(id)).thenReturn(new ProductResponseDTO(createStandardProduct()));
		
		mockMvc.perform(
				get("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Coca Lata"))
				.andExpect(jsonPath("$.category").value(Category.BEBIDAS.toString()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find object")
	void findByIdResourceNotFoundCase() throws Exception {
		Long id = 99L;
		
		when(service.findById(id)).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(
				get("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	@DisplayName("Should return 201 created and the location header")
	void insertSuccessCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO("Coca Lata", Category.BEBIDAS);
		ProductResponseDTO outputDTO = new ProductResponseDTO(createStandardProduct());
		
		when(service.insert(any(ProductRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Coca Lata"))
				.andExpect(jsonPath("$.category").value(Category.BEBIDAS.toString()))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 409 Conflit when already exists a product with the name")
	void insertConflitNameCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO("Coca Lata", Category.BEBIDAS);
		
		when(service.insert(any(ProductRequestDTO.class))).thenThrow(ProductExistsException.class);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.error").value("Resource conflit"));
	}
	
	@Test
	@DisplayName("Should return 422 Unprocessable Entity when any field isn't valid")
	void insertValidationsExceptionCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO();
		inputDTO.setCategory(Category.BEBIDAS);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.errors").isArray());
	}
	
	@Test
	@DisplayName("Should return 200 Ok when updating product")
	void updateSuccessCase() throws Exception {
		Long id = 1L;
		ProductRequestDTO inputDTO = new ProductRequestDTO("Coca Lata", Category.BEBIDAS);
		
		ProductResponseDTO outputDTO = new ProductResponseDTO(createStandardProduct());
		
		when(service.update(eq(id), any(ProductRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Coca Lata"))
				.andExpect(jsonPath("$.category").value(Category.BEBIDAS.name()));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find object")
	void updateResourceNotFoundCase() throws Exception {
		Long id = 99L;
		ProductRequestDTO inputDTO = new ProductRequestDTO("Coca Lata", Category.BEBIDAS);
		
		when(service.update(eq(id), any(ProductRequestDTO.class))).thenThrow(ResourceNotFoundException.class);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	@DisplayName("Should return 204 No Content when deleting product")
	void deleteSuccessCase() throws Exception {
		Long id = 1L;
		
		mockMvc.perform(
				delete("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find product")
	void deleteResourceNotFoundCase() throws Exception {
		Long id = 99L;
		
		doThrow(ResourceNotFoundException.class).when(service).delete(id);
		
		mockMvc.perform(
				delete("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when deleting product with depedencies")
	void deleteIntegrityViolationCase() throws Exception {
		Long id = 99L;
		
		doThrow(DatabaseException.class).when(service).delete(id);
		
		mockMvc.perform(
				delete("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Database error"));
	}
	
	private Product createStandardProduct() {
		Product product = new Product("Coca Lata", Category.BEBIDAS);
		ReflectionTestUtils.setField(product, "id", 1L);
		return product;
	}
}
