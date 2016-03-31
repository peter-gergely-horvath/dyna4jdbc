package com.github.dyna4jdbc.internal;

public class RuntimeDyna4JdbcException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuntimeDyna4JdbcException(String message) {
		super(message);
	}

	public RuntimeDyna4JdbcException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuntimeDyna4JdbcException(Throwable cause) {
		super(cause);
	}
}
