package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

public enum JDBCError {

    CONNECT_FAILED_INVALID_URL("Invalid URL: %s"),
    CONNECT_FAILED_GENERIC("Failed to connect: %s (examine stack trace for details)"),
    OBJECT_CLOSED("Object has already been closed: %s"),
    SCRIPT_EXECUTION_EXCEPTION("Execution of script raised exception: %s"),
    CANNOT_UNWARP_OBJECT("The requested type (%s) cannot be unwrapped from this object (%s)."),
    UNEXPECTED_THROWABLE("Processing failed; caught unexpected exception: %s"),
    RESULT_SET_MULTIPLE_EXPECTED_ONE("Expected one result set, but script produced %s result sets."),
    JDBC_FUNCTION_NOT_SUPPORTED("This JDBC API function is not supported: %s"),
    USING_STDOUT_FROM_UPDATE("Using standard output from an update call is not permitted"),
    DATA_CONVERSION_FAILED("Data conversion failed in row %1$s, column %2$s. Value '%3$s' could not be converted to %4$s."),
    INVALID_CONFIGURATION_HEADER("Encountered invalid configuration header: %s"),
    JDBC_API_USAGE_CALLER_ERROR("Illegal JDBC API call: %s"),
    INVALID_CONFIGURATION("Configuration error: %s"),
    DRIVER_BUG_UNEXPECTED_STATE("An unexpected state has been reached: %s"); 


    private final String message;

    JDBCError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("JDBC Error [%s]", this.name());
    }
    
    protected String getMessageTemplate() {
    	return String.format("JDBC Error [%s]: %s", this.name(), this.message);
    }

    public SQLException raiseSQLException(Exception ex, Object... params) throws SQLException {
        throw new SQLException(String.format(getMessageTemplate(), params), ex);
    }

    public SQLException raiseSQLException(Object... params) throws SQLException {
        throw new SQLException(String.format(getMessageTemplate(), params));
    }
    
    public RuntimeDyna4JdbcException raiseUncheckedException(Exception ex, Object... params) throws RuntimeDyna4JdbcException {
        throw new RuntimeDyna4JdbcException(String.format(getMessageTemplate(), params), ex);
    }
    
    public RuntimeDyna4JdbcException raiseUncheckedException(Object... params) throws RuntimeDyna4JdbcException {
    	throw new RuntimeDyna4JdbcException(String.format(getMessageTemplate(), params));
    }

}
