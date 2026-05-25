package com.reis.management_control_API.IntegrationTests;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.reis.ManagementControl_API.ManagementControlApiApplication;
import com.reis.ManagementControl_API.Entities.Product;
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
				.andExpect(jsonPath("$[0].id").value(1L))
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
				.andExpect(jsonPath("$[0].id").value(1L))
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
}
