package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.sqlstate.SQLState;

public enum JDBCError {

    CONNECT_FAILED_INVALID_URL("Invalid URL: %s", SQLState.ERROR_CONNECTION_UNABLE_TO_ESTABILISH),
    CONNECT_FAILED_EXCEPTION("Failed to connect: %s (examine stack trace for details)", SQLState.ERROR_CONNECTION_UNABLE_TO_ESTABILISH),
    OBJECT_CLOSED("Object has already been closed: %s", null),
    CLOSE_FAILED("Closing of '%s' caused error: %s", SQLState.CLIENT_ERROR),
    SCRIPT_EXECUTION_EXCEPTION("Execution of script raised exception: %s", SQLState.DYNAMIC_SQL_ERROR),
    CANNOT_UNWARP_OBJECT("The requested type (%s) cannot be unwrapped from this object (%s).", SQLState.CLIENT_ERROR),
    UNEXPECTED_THROWABLE("Processing failed; caught unexpected exception: %s", SQLState.SYSTEM_ERROR),
    RESULT_SET_MULTIPLE_EXPECTED_ONE("Expected one result set, but script produced %s result sets.", SQLState.CLIENT_ERROR),
    INCONSISTENT_HEADER_SPECIFICATION("Inconsistent header specification: %s", SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    DUPLICATED_HEADER_NAME("Duplicated header name encountered: %s", SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    FORMAT_STRING_UNEXPECTED_FOR_COLUMN_TYPE("Format string '%s' unexpected for column type: %s", SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    FORMAT_STRING_INVALID("Format string '%s' is illegal: %s", SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    JDBC_FUNCTION_NOT_SUPPORTED("This JDBC API function is not supported: %s", SQLState.FEATURE_NOT_SUPPORTED),
    USING_STDOUT_FROM_UPDATE("Using standard output from an update call is not permitted", SQLState.EXTERNAL_ROUTINE_INVOCATION_EXCEPTION),
    UNSUPPORTED_CONVERSION("Conversion of value '%s' to the requeste type is not possible %s", SQLState.ERROR_DATA_CONVERSION_NOT_SUPPORTED),
    DATA_CONVERSION_FAILED("Data conversion failed in row %1$s, column %2$s. Value '%3$s' could not be converted to %4$s.", SQLState.ERROR_DATA_CONVERSION_FAILED),
    INVALID_CONFIGURATION_HEADER("Encountered invalid configuration header: %s", SQLState.CLIENT_ERROR),
    JDBC_API_USAGE_CALLER_ERROR("Illegal JDBC API call: %s", SQLState.CLIENT_ERROR),
    INVALID_CONFIGURATION("Configuration error: %s", SQLState.CLIENT_ERROR),
    DRIVER_BUG_UNEXPECTED_STATE("An unexpected state has been reached: %s", SQLState.SYSTEM_ERROR); 


    private final String message;
	private final SQLState sqlState;

    JDBCError(String message, SQLState sqlState) {
        this.message = message;
        this.sqlState = sqlState;
    }

    @Override
    public String toString() {
        return String.format("JDBC Error [%s]", this.name());
    }
    
    protected String getMessageTemplate() {
    	return String.format("JDBC Error [%s]: %s", this.name(), this.message);
    }
    
    protected String getSqlStateAsString() {
    	return sqlState != null ? sqlState.code : null;
    }

    public SQLException raiseSQLException(Throwable throwable, Object... params) throws SQLException {
        String errorMessage = buildErrorMessage(params);
        throw new SQLException(errorMessage, getSqlStateAsString(), throwable);
    }
    
    public SQLException raiseSQLExceptionWithSupressed(
            Iterable<? extends Throwable> supressedThrowables,
            Object... params) throws SQLException {

        String errorMessage = buildErrorMessage(params);
        SQLException sqlException = new SQLException(errorMessage, getSqlStateAsString() );
        
        for(Throwable supressed : supressedThrowables) {
        	sqlException.addSuppressed(supressed);
        }
        throw sqlException;
    }

    public SQLException raiseSQLException(Object... params) throws SQLException {
        String errorMessage = buildErrorMessage(params);
        throw new SQLException(errorMessage, getSqlStateAsString() );
    }
    
    public RuntimeDyna4JdbcException raiseUncheckedException(Throwable throwable, Object... params) throws RuntimeDyna4JdbcException {
        String errorMessage = buildErrorMessage(params);
        throw new RuntimeDyna4JdbcException(errorMessage, throwable, getSqlStateAsString() );
    }
    
    public RuntimeDyna4JdbcException raiseUncheckedException(Object... params) throws RuntimeDyna4JdbcException {
        String errorMessage = buildErrorMessage(params);
        throw new RuntimeDyna4JdbcException(errorMessage, getSqlStateAsString() );
    }

    private String buildErrorMessage(Object[] params) {
        String formatString = getMessageTemplate();
        return String.format(formatString, params);
    }

}
