package com.reis.ManagementControl_API.Services.Exceptions;

public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException (Long id) {
		super("Id não encontrado. Id:" + id);
	}

}
