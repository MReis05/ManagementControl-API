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
import com.reis.ManagementControl_API.Entities.Location;
import com.reis.ManagementControl_API.Entities.DTO.LocationRequestDTO;
import com.reis.ManagementControl_API.Repositories.LocationRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(classes = ManagementControlApiApplication.class)
@Transactional
public class LocationIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private LocationRepository repository;
	
	@Autowired
	private ObjectMapper mapper;
	
	private Long id;
	
	@BeforeEach
	void injectObjects() {
		Location location = new Location("Atacadão");
		
		location = repository.save(location);
		
		this.id = location.getId();
	}
	
	@Test
	@DisplayName("Should return 200 OK and a List<LocationResponseDTO> (End-To-End)")
	void findAllSuccessCase() throws Exception {
		mockMvc.perform(
				get("/locations")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(id))
				.andExpect(jsonPath("$[0].name").value("Atacadão"));
	}
	
	@Test
	@DisplayName("Should return 200 OK and a LocationResponseDTO (End-To-End)")
	void findByIdSuccessCase() throws Exception {
		mockMvc.perform(
				get("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Atacadão"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find object (End-To-End)")
	void findByIdResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				get("/locations/" + (id + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (id + 98)));
	}
	
	@Test
	@DisplayName("Should create a location in database and return 201 Created (End-To-End)")
	void insertSuccessCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO("Mix Mateus");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/locations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Mix Mateus"))
				.andExpect(header().exists("Location"));
		
		Location savedLocation = repository.findAll().stream().filter(l -> "Mix Mateus".equals(l.getName())).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 2);
		assertEquals(inputDTO.getName(), savedLocation.getName());
	}
	
	@Test
	@DisplayName("Should return 409 Conflit when already exists a location with the name (End-To-End)")
	void insertDataConflitCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO("Atacadão");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/locations")
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
		LocationRequestDTO inputDTO = new LocationRequestDTO();
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/locations")
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
	@DisplayName("Should update location in database and return 200 OK (End-To-End)")
	void updateSuccessCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO("Mix Mateus");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Mix Mateus"));
		
		Location savedLocation = repository.findAll().stream().filter(l -> "Mix Mateus".equals(l.getName())).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 1);
		assertEquals(id, savedLocation.getId());
		assertEquals(inputDTO.getName(), savedLocation.getName());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't update location when doesn't find object (End-To-End)")
	void updateResourceNotFoundCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO("Mix Mateus");
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/locations/" + (id + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"))
				.andExpect(jsonPath("$.message").value("Id não encontrado. Id:" + (id + 98)));
		
		Location savedLocation = repository.findAll().stream().filter(l -> "Atacadão".equals(l.getName())).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 1);
		assertEquals("Atacadão", savedLocation.getName());
	}
	
	@Test
	@DisplayName("Should return 422 Unprocessable Entity and don't update location when any field isn't valid")
	void UpdateValidationsExceptionCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO();
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isUnprocessableEntity())
				.andExpect(jsonPath("$.status").value(422))
				.andExpect(jsonPath("$.error").value("Validation Error"))
				.andExpect(jsonPath("$.errors").isArray());
		
		Location savedLocation = repository.findAll().stream().filter(l -> "Atacadão".equals(l.getName())).findFirst().orElseThrow();
		
		assertEquals(repository.count(), 1);
		assertEquals("Atacadão", savedLocation.getName());
	}
	
	@Test
	@DisplayName("Should return 204 No Content and delete location in database (End-To-End)")
	void deleteSuccessCase() throws Exception {
		mockMvc.perform(
				delete("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
		
		assertEquals(repository.count(), 0);
	}
	
	@Test
	@DisplayName("Should return 404 Not Found and don't delete location in database (End-To-End)")
	void deleteResourceNotFoundCase() throws Exception {
		mockMvc.perform(
				delete("/locations/" + (id + 98L))
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
		
		assertEquals(repository.count(), 1);
	}
}
