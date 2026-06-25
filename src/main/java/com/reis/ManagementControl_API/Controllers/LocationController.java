package com.reis.ManagementControl_API.Controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.reis.ManagementControl_API.Entities.DTO.LocationRequestDTO;
import com.reis.ManagementControl_API.Entities.DTO.LocationResponseDTO;
import com.reis.ManagementControl_API.Services.LocationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/locations")
public class LocationController {

	private final LocationService service;

	LocationController(LocationService service) {
		this.service = service;
	}
	
	@GetMapping
	public ResponseEntity<List<LocationResponseDTO>> findAll(){
		List<LocationResponseDTO> dto = service.findAll();
		return ResponseEntity.ok().body(dto);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<LocationResponseDTO> findById(@PathVariable Long id){
		LocationResponseDTO dto = service.findById(id);
		return ResponseEntity.ok().body(dto);
	}
	
	@PostMapping
	public ResponseEntity<LocationResponseDTO> insert(@Valid @RequestBody LocationRequestDTO dto){
		LocationResponseDTO resp = service.insert(dto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(resp.getId()).toUri();
		return ResponseEntity.created(uri).body(resp);
	}
	
	@PutMapping(value = "/{id}")
	public ResponseEntity<LocationResponseDTO> update(@PathVariable Long id, @Valid @RequestBody LocationRequestDTO dto){
		LocationResponseDTO resp = service.update(id, dto);
		return ResponseEntity.ok().body(resp);
	}
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
