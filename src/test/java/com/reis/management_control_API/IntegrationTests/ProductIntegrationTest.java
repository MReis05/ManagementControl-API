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
	
	@BeforeEach
	void injectObjects() {
		Product product = new Product("Coca Lata", Category.BEBIDAS);
		
		product = repository.save(product);
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
}
