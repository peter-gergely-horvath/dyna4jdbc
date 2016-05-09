package com.github.dyna4jdbc.internal;

public class RuntimeDyna4JdbcException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    private final String sqlState;

	public RuntimeDyna4JdbcException(String message, String sqlState) {
		super(message);
        this.sqlState = sqlState;
	}

	public RuntimeDyna4JdbcException(String message, Throwable cause, String sqlState) {
		super(message, cause);
        this.sqlState = sqlState;
	}

    public String getSqlState() {
        return sqlState;
    }

}
