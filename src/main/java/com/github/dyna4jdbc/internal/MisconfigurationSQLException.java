package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

public class MisconfigurationSQLException extends SQLException {

	private static final long serialVersionUID = 1L;
	
	public MisconfigurationSQLException(String message) {
		super(message);
	}
	
	public static MisconfigurationSQLException forMessage(String format, Object... args) {
		return new MisconfigurationSQLException(String.format(format, args));
	}

}
