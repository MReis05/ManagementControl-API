package com.reis.ManagementControl_API.Services.Exceptions;

public class ProductExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ProductExistsException() {
		super("Esse produto já existe em nosso banco de dados");
	}
}
