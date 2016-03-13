package com.github.dyna4jdbc.internal;

import com.github.dyna4jdbc.DynaDriver;

import java.sql.SQLException;

public enum SQLError {

    CONNECT_FAILED_INVALID_URL("Invalid URL: %s"),
    CONNECT_FAILED_GENERIC("Failed to connect: %s"),
    SCRIPT_EXECUTION_EXCEPTION("Execution of script raised exception: examine stack trace for root cause."),
    UNEXPECTED_THROWABLE("Processing failed: caught unexpected exception."),
    RESULT_SET_MULTIPLE_EXPECTED_ONE("Expected one result set, but script produced %s result sets."),
    JDBC_FUNCTION_NOT_SUPPORTED("This JDBC API function is not supported: %s"),
    USING_STDOUT_FROM_UPDATE("Using standard output from an update call is not permitted");


    private final String message;

    SQLError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("%s SQLError [%s]", DynaDriver.DRIVER_SHORT_NAME, this.name());
    }
    
    private String getMessageTemplate() {
    	return String.format("%s SQLError [%s]: %s", DynaDriver.DRIVER_SHORT_NAME, this.name(), this.message);
    }

    public SQLException raiseException(Exception ex, Object... params) throws SQLException {
        throw new SQLException(String.format(getMessageTemplate(), params), ex);
    }

    public SQLException raiseException(Object... params) throws SQLException {
        throw new SQLException(String.format(getMessageTemplate(), params));
    }
}
