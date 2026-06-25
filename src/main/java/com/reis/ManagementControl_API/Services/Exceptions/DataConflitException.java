package com.reis.ManagementControl_API.Services.Exceptions;

public class DataConflitException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataConflitException() {
		super("Esse objeto já existe em nosso banco de dados");
	}
}
