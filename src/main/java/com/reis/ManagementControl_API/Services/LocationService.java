package com.reis.ManagementControl_API.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reis.ManagementControl_API.Entities.Location;
import com.reis.ManagementControl_API.Entities.DTO.LocationRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.LocationResponseDTO;
import com.reis.ManagementControl_API.Repositories.LocationRepository;
import com.reis.ManagementControl_API.Services.Exceptions.DataConflitException;
import com.reis.ManagementControl_API.Services.Exceptions.DatabaseException;
import com.reis.ManagementControl_API.Services.Exceptions.ResourceNotFoundException;

@Service
public class LocationService {

	private final LocationRepository repository;

	LocationService(LocationRepository repository) {
		this.repository = repository;
	}
	
	@Transactional(readOnly = true)
	public List<LocationResponseDTO> findAll(){
		return repository.findAll().stream().map(LocationResponseDTO::new).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public LocationResponseDTO findById(Long id) {
		Location location = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		return new LocationResponseDTO(location);
	}
	
	@Transactional
	public LocationResponseDTO insert (LocationRequestDTO dto) {
		if(existsByNameIgnoreCase(dto.getName())){
			throw new DataConflitException();
		}
		
		Location location = new Location(dto.getName());
		location = repository.save(location);
		return new LocationResponseDTO(location);
	}
	
	@Transactional
	public LocationResponseDTO update(Long id, LocationRequestDTO dto) {
		Location location = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
		location.setName(dto.getName());
		
		location = repository.save(location);
		
		return new LocationResponseDTO(location);
	}
	
	@Transactional
	public void delete(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException(id);
		}
		
		try {
			repository.deleteById(id);
		}
		catch(DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	@Transactional(readOnly = true)
	public boolean existsByNameIgnoreCase(String name) {
		return repository.existsByNameIgnoreCase(name);
	}	
}
