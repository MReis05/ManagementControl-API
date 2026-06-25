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

import com.reis.ManagementControl_API.Entities.Location;
import com.reis.ManagementControl_API.Entities.DTO.LocationRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.LocationResponseDTO;
import com.reis.ManagementControl_API.Repositories.LocationRepository;
import com.reis.ManagementControl_API.Services.LocationService;
import com.reis.ManagementControl_API.Services.Exceptions.DataConflitException;
import com.reis.ManagementControl_API.Services.Exceptions.DatabaseException;
import com.reis.ManagementControl_API.Services.Exceptions.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

	@Mock
	private LocationRepository repository;
	
	@InjectMocks
	private LocationService service;
	
	@Test
	@DisplayName("Should return a List<LocationReponseDTO> when find objects")
	void findAllSuccessCase() {
		List<Location> listExpected = List.of(createStandardLocation());
		
		when(repository.findAll()).thenReturn(listExpected);
		
		List<LocationResponseDTO> listReceived = service.findAll();
		
		assertEquals(listExpected.size(), listReceived.size());
		assertEquals(listExpected.get(0).getId(), listReceived.get(0).getId());
		assertEquals(listExpected.get(0).getName(), listReceived.get(0).getName());
		
		verify(repository).findAll();
	}
	
	@Test
	@DisplayName("Should return a LocationResponseDTO when find object")
	void findByIdSuccessCase() {
		Long id = 1L;
		Location locationExpected = createStandardLocation();
		
		when(repository.findById(id)).thenReturn(Optional.of(locationExpected));
		
		LocationResponseDTO locationReceived = service.findById(id);
		
		assertEquals(locationExpected.getId(), locationReceived.getId());
		assertEquals(locationExpected.getName(), locationReceived.getName());
		
		verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void findByIdResourceNotFoundCase() {
		Long id = 99L;
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,() ->{
			service.findById(id);
		});
		
		assertNotNull(exception.getMessage());
		assertEquals(ResourceNotFoundException.class, exception.getClass());
		
		verify(repository).findById(id);
	}
	
	@Test
	@DisplayName("Should save location and return LocationResponseDTO")
	void insertSuccessCase() {
		LocationRequestDTO dto = new LocationRequestDTO("Mix Mateus");
		
		when(repository.save(any(Location.class))).thenAnswer(invocation ->{
			Location location = invocation.getArgument(0);
			ReflectionTestUtils.setField(location, "id", 1L);
			return location;
		});
		
		LocationResponseDTO locationReceived = service.insert(dto);
		
		assertNotNull(locationReceived);
		assertEquals(dto.getName(), locationReceived.getName());
		
		verify(repository).save(any(Location.class));
	}
	
	@Test
	@DisplayName("Should throw a DataConflitException when already exists a location with the name")
	void insertDataConflitCase() {
		LocationRequestDTO dto = new LocationRequestDTO("Mix Mateus");
		
		when(repository.existsByNameIgnoreCase("Mix Mateus")).thenReturn(true);
		
		DataConflitException exception = assertThrows(DataConflitException.class, () ->{
			service.insert(dto);
		});
		
		assertNotNull(exception);
		assertEquals(DataConflitException.class, exception.getClass());
		assertEquals("Esse objeto já existe em nosso banco de dados", exception.getMessage());
		
		verify(repository, never()).save(any());
	} 
	
	@Test
	@DisplayName("Should update location and return a LocationResponseDTO")
	void updateSuccessCase() {
		Long id = 1L;
		Location obj = createStandardLocation();
		
		when(repository.findById(id)).thenReturn(Optional.of(obj));
		
		LocationRequestDTO dto = new LocationRequestDTO("Mix Mateus");
		
		when(repository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));
		
		LocationResponseDTO locationReceived = service.update(id, dto);
		
		assertEquals(obj.getId(), locationReceived.getId());
		assertEquals(dto.getName(), locationReceived.getName());
		
		verify(repository).findById(any());
		verify(repository).save(any());
	}
	
	@Test
	@DisplayName("Should throw a ResourceNotFoundException when doesn't find object")
	void updateResourceNotFoundCase() {
		Long id = 99L;
		
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		LocationRequestDTO dto = new LocationRequestDTO();
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
	@DisplayName("Should delete location when id exists and has no dependencies")
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
	@DisplayName("Should throw DatabaseException when deleting location with dependencies")
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
	
	private Location createStandardLocation() {
		Location location = new Location("Atacadão");
		ReflectionTestUtils.setField(location, "id", 1L);
		return location;
	}
}
