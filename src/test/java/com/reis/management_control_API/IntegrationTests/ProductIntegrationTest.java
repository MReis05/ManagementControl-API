package com.reis.management_control_API.IntegrationTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reis.ManagementControl_API.ManagementControlApiApplication;
import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.DTO.ProductRequestDTO;
import com.reis.ManagementControl_API.Entities.Enums.Category;
import com.reis.ManagementControl_API.Repositories.ProductRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = ManagementControlApiApplication.class)
@Transactional
public class ProductIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Long id;
	
	@BeforeEach
	void injectObjects() {
		Product product = new Product("Coca Lata", Category.BEBIDAS);
		
		product = repository.save(product);
		
		this.id = product.getId();
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of Products (End-To-End)")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/products")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id))
				.andExpect(jsonPath("$[0].name").value("Coca Lata"))
				.andExpect(jsonPath("$[0].category").value(Category.BEBIDAS.toString()));
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a List of Product with right name(End-to-End)")
	void findByNameSuccessCase() throws Exception {
		mockMvc.perform(
				get("/products?name=Coca Lata")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id))
				.andExpect(jsonPath("$[0].name").value("Coca Lata"))
				.andExpect(jsonPath("$[0].category").value(Category.BEBIDAS.toString()));
	}
	
	@Test
	@DisplayName("Should return an empty list and 200 Ok status (End-To-End")
	void findByNameNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/products?name=Coca Litro")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$").isEmpty());
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a ProductResponseDTO when find objeect (End-to-End)")
	void findByIdSuccessCase() throws Exception {
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
	@DisplayName("Should throw a ResourceNotFoundException and 404 Not found status when doesn' find object (End-to-End)")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/products/" + (id + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (id + 98)));
	}
	
	@Test
	@DisplayName("Should create a Product in Database and return 201 Created status (End-to-End)")
	void insertSuccessCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO("Farinha de Trigo", Category.INSUMOS_SECUNDARIOS);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value("Farinha de Trigo"))
				.andExpect(jsonPath("$.category").value(Category.INSUMOS_SECUNDARIOS.name()))
				.andExpect(header().exists("Location"));
		
		Product savedProduct = repository.findByNameContainingIgnoreCase("Farinha de Trigo").getFirst();
		
		assertEquals(repository.count(), 2);
		assertEquals(inputDTO.getName(), savedProduct.getName());
		assertEquals(inputDTO.getCategory(), savedProduct.getCategory());
	}
	
	@Test
	@DisplayName("Should return 409 Conflit when already exists a product with the name")
	void insertConflitNameCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO("Coca Lata", Category.BEBIDAS);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.status").value(409))
				.andExpect(jsonPath("$.error").value("Resource conflit"));
		
		assertEquals(repository.count(), 1);
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
		
		assertEquals(repository.count(), 1);
	}
	
	@Test
	@DisplayName("Should return 200 Ok and update product in database (End-to-End)")
	void updateSuccessCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO();
		inputDTO.setName("Coca de 1 Litro");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Coca de 1 Litro"))
				.andExpect(jsonPath("$.category").value(Category.BEBIDAS.name()));
		
		Product obj = repository.findById(id).orElseThrow();
		
		assertEquals(repository.count(), 1);
		assertEquals(obj.getId(), id);
		assertEquals(obj.getName(), "Coca de 1 Litro");
		assertEquals(obj.getCategory(), Category.BEBIDAS);
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't update product in database (End-to-End)")
	void updateResourceNotFoundCase() throws Exception {
		ProductRequestDTO inputDTO = new ProductRequestDTO();
		inputDTO.setName("Coca de 1 Litro");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/products/" + (id + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
		
		Product obj = repository.findById(id).orElseThrow();
		
		assertEquals(repository.count(), 1);
		assertEquals(obj.getName(), "Coca Lata");
	}
	
	@Test
	@DisplayName("Should return 204 No Content and delete product in database (End-to-End)")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/products/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
		
		assertEquals(repository.count(), 0);
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't delete product in database (End-to-End)")
	void deleteResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				delete("/products/" + (id + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
		
		assertEquals(repository.count(), 1);
	}
}
