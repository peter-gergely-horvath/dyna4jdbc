package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

/**
 * @author Peter Horvath
 */
public class UnsupportedOperationSQLException extends SQLException {

    public UnsupportedOperationSQLException(String message, String sqlState) {
        super(message, sqlState);
    }
}
