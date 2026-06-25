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
import com.reis.ManagementControl_API.Controllers.LocationController;
import com.reis.ManagementControl_API.Entities.Location;
import com.reis.ManagementControl_API.Entities.DTO.LocationRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.LocationResponseDTO;
import com.reis.ManagementControl_API.Services.LocationService;
import com.reis.ManagementControl_API.Services.Exceptions.DataConflitException;
import com.reis.ManagementControl_API.Services.Exceptions.DatabaseException;
import com.reis.ManagementControl_API.Services.Exceptions.ResourceNotFoundException;

@WebMvcTest(LocationController.class)
@ContextConfiguration(classes = ManagementControlApiApplication.class)
public class LocationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockitoBean
	private LocationService service;
	
	@Test
	@DisplayName("Should return 200 OK and a List of location")
	void findAllSuccessCase() throws Exception {
		LocationResponseDTO dto = new LocationResponseDTO(createStandardLocation());
		
		when(service.findAll()).thenReturn(List.of(dto));
		
		mockMvc.perform(
				get("/locations")
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].name").value("Atacadão"));
	}
	
	@Test
	@DisplayName("Should return 200 Ok and a LocationResponseDTO")
	void findByIdSuccessCase() throws Exception {
		Long id = 1L;
		
		LocationResponseDTO dto = new LocationResponseDTO(createStandardLocation());
		
		when(service.findById(id)).thenReturn(dto);
		
		mockMvc.perform(
				get("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("Atacadão"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find object")
	void findByIdResourceNotFoundCase() throws Exception {
		Long id = 99L;
		
		when(service.findById(id)).thenThrow(ResourceNotFoundException.class);
		
		mockMvc.perform(
				get("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	@DisplayName("Should return 201 created and the location header")
	void insertSuccessCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO("Atacadão");
		LocationResponseDTO outputDTO = new LocationResponseDTO(createStandardLocation());
		
		when(service.insert(any(LocationRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/locations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Atacadão"))
				.andExpect(header().exists("Location"));
	}
	
	@Test
	@DisplayName("Should return 409 Conflit when already exists a location with the name")
	void insertConflitNameCase() throws Exception {
		LocationRequestDTO inputDTO = new LocationRequestDTO("Atacadão");
		
		when(service.insert(any(LocationRequestDTO.class))).thenThrow(DataConflitException.class);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				post("/locations")
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
	}
	
	@Test
	@DisplayName("Should return 200 Ok when updating location")
	void updateSuccessCase() throws Exception {
		Long id = 99L;
		
		LocationRequestDTO inputDTO = new LocationRequestDTO("Atacadão");
		LocationResponseDTO outputDTO = new LocationResponseDTO(createStandardLocation());
		
		when(service.update(eq(id), any(LocationRequestDTO.class))).thenReturn(outputDTO);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		mockMvc.perform(
				put("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value("Atacadão"));
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find object")
	void updateResourceNotFoundCase() throws Exception {
		Long id = 99L;
		
		LocationRequestDTO inputDTO = new LocationRequestDTO("Atacadão");
		
		when(service.update(eq(id), any(LocationRequestDTO.class))).thenThrow(ResourceNotFoundException.class);
		
		String jsonBody = mapper.writeValueAsString(inputDTO);
		
		
		mockMvc.perform(
				put("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonBody)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	@DisplayName("Should return 422 Unprocessable Entity when any field isn't valid")
	void updateValidationsExceptionCase() throws Exception {
		Long id = 99L;
		
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
	}
	
	@Test
	@DisplayName("Should return 204 No Content when deleting location")
	void deleteSuccessCase() throws Exception {
		Long id = 1L;
		
		mockMvc.perform(
				delete("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return 404 Not Found when doesn't find location")
	void deleteResourceNotFoundCase() throws Exception {
		Long id = 99L;
		
		doThrow(ResourceNotFoundException.class).when(service).delete(id);
		
		mockMvc.perform(
				delete("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.error").value("Resource not found"));
	}
	
	@Test
	@DisplayName("Should return 400 Bad Request when deleting location with depedencies")
	void deleteIntegrityViolationCase() throws Exception {
		Long id = 99L;
		
		doThrow(DatabaseException.class).when(service).delete(id);
		
		mockMvc.perform(
				delete("/locations/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.error").value("Database error"));
	}
	
	private Location createStandardLocation() {
		Location location = new Location("Atacadão");
		ReflectionTestUtils.setField(location, "id", 1L);
		return location;
	}
}
