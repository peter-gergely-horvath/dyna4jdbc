package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

/**
 * @author Peter Horvath
 */
public class UnsupportedOperationSQLException extends SQLException {

    private static final long serialVersionUID = 1L;

    public UnsupportedOperationSQLException(String message, String sqlState) {
        super(message, sqlState);
    }
}
