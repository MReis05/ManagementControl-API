package com.reis.management_control_API.Controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import com.reis.ManagementControl_API.ManagementControlApiApplication;
import com.reis.ManagementControl_API.Controllers.ProductController;
import com.reis.ManagementControl_API.Entities.Product;
import com.reis.ManagementControl_API.Entities.DTO.ProductResponseDTO;
import com.reis.ManagementControl_API.Entities.Enums.Category;
import com.reis.ManagementControl_API.Services.ProductService;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = ManagementControlApiApplication.class)
public class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
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
	@DisplayName("Shoudl return 200 Ok and a List of Products with right name")
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
	
	private Product createStandardProduct() {
		Product product = new Product("Coca Lata", Category.BEBIDAS);
		ReflectionTestUtils.setField(product, "id", 1L);
		return product;
	}
}
