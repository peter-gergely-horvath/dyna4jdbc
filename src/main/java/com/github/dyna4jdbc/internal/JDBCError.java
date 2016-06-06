package com.github.dyna4jdbc.internal;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.sqlstate.SQLState;

public enum JDBCError {

    CONNECT_FAILED_INVALID_URL("Invalid URL: %s",
            SQLState.ERROR_CONNECTION_UNABLE_TO_ESTABILISH),
    CONNECT_FAILED_EXCEPTION("Failed to connect: %s (examine stack trace for details)",
            SQLState.ERROR_CONNECTION_UNABLE_TO_ESTABILISH),
    OBJECT_CLOSED("Object has already been closed: %s", null),
    CLOSE_FAILED("Closing of '%s' caused error: %s",
            SQLState.CLIENT_ERROR),
    EXECUTION_ABORTED_AT_CLIENT_REQUEST("Execution aborted at client request",
            SQLState.CLIENT_ABORT),
    SCRIPT_EXECUTION_EXCEPTION("Execution of script raised exception: %s",
            SQLState.DYNAMIC_SQL_ERROR),
    CANNOT_UNWARP_OBJECT("The requested type (%s) cannot be unwrapped from this object (%s).",
            SQLState.CLIENT_ERROR),
    UNEXPECTED_THROWABLE("Processing failed; caught unexpected exception: %s",
            SQLState.SYSTEM_ERROR),
    RESULT_SET_MULTIPLE_EXPECTED_ONE("Expected one result set, but script produced %s result sets.",
            SQLState.CLIENT_ERROR),
    INCONSISTENT_HEADER_SPECIFICATION("Inconsistent header specification: %s",
            SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    DUPLICATED_HEADER_NAME("Duplicated header name encountered: %s",
            SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    FORMAT_STRING_UNEXPECTED_FOR_COLUMN_TYPE("Format string '%s' unexpected for column type: %s",
            SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    FORMAT_STRING_INVALID("Format string '%s' is illegal: %s",
            SQLState.SYNTAX_OR_ACCESS_RULE_ERROR),
    CANCEL_REQUESTED_ALREADY("Cancellation requested already: %s",
            SQLState.CLIENT_ERROR),
    CANCEL_FAILED("Failed to cancel the operation: %s",
            SQLState.SYSTEM_ERROR),
    JDBC_FUNCTION_NOT_SUPPORTED("This JDBC API function is not supported: %s",
            SQLState.FEATURE_NOT_SUPPORTED) {
        @Override
        public SQLException raiseSQLException(Object... params) throws SQLException {
            String errorMessage = buildErrorMessage(params);
            throw new UnsupportedOperationSQLException(errorMessage, getSqlStateAsString());
        }
    },
    USING_STDOUT_FROM_UPDATE("Using standard output from an update call is not permitted",
            SQLState.EXTERNAL_ROUTINE_INVOCATION_EXCEPTION),
    UNSUPPORTED_CONVERSION("Conversion of value '%s' to the requeste type is not possible %s",
            SQLState.ERROR_DATA_CONVERSION_NOT_SUPPORTED),
    DATA_CONVERSION_FAILED(
            "Data conversion failed in row %1$s, column %2$s. Value '%3$s' could not be converted to %4$s.",
            SQLState.ERROR_DATA_CONVERSION_FAILED),
    INVALID_FORMATTING_HEADER("Invalid formatting header detected in column %s: Value '%s' is invalid: %s",
            SQLState.CLIENT_ERROR),
    JDBC_API_USAGE_CALLER_ERROR("Illegal JDBC API call: %s",
            SQLState.CLIENT_ERROR),
    LOADING_SCRIPTENGINE_FAILED("Could not load ScriptEngine '%s'",
            SQLState.SYSTEM_ERROR),
    INVALID_CONFIGURATION("Configuration error: %s",
            SQLState.CLIENT_ERROR),
    DRIVER_BUG_UNEXPECTED_STATE("An unexpected state has been reached: %s",
            SQLState.SYSTEM_ERROR);


    private static final int VENDOR_CODE_COMPLETION_FAILURE = 2;

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
        return String.format("[%s]: %s", this.name(), this.message);
    }

    protected String getSqlStateAsString() {
        //CHECKSTYLE.OFF: AvoidInlineConditionals
        return sqlState != null ? sqlState.code : null;
        //CHECKSTYLE.ON: AvoidInlineConditionals
    }

    public SQLException raiseSQLException(Throwable throwable, Object... params) throws SQLException {
        String errorMessage = buildErrorMessage(params);
        throw new SQLException(errorMessage, getSqlStateAsString(), throwable);
    }

    public SQLException raiseSQLExceptionWithSupressed(
            Iterable<? extends Throwable> supressedThrowables,
            Object... params) throws SQLException {

        String errorMessage = buildErrorMessage(params);
        SQLException sqlException = new SQLException(errorMessage, getSqlStateAsString());

        for (Throwable supressed : supressedThrowables) {
            sqlException.addSuppressed(supressed);
        }
        throw sqlException;
    }

    public SQLException raiseSQLException(Object... params) throws SQLException {
        String errorMessage = buildErrorMessage(params);
        throw new SQLException(errorMessage, getSqlStateAsString(), VENDOR_CODE_COMPLETION_FAILURE);
    }

    public RuntimeDyna4JdbcException raiseUncheckedException(
            Throwable throwable, Object... params) throws RuntimeDyna4JdbcException {

        String errorMessage = buildErrorMessage(params);
        throw new RuntimeDyna4JdbcException(errorMessage, throwable, getSqlStateAsString());
    }

    public RuntimeDyna4JdbcException raiseUncheckedException(Object... params) throws RuntimeDyna4JdbcException {
        String errorMessage = buildErrorMessage(params);
        throw new RuntimeDyna4JdbcException(errorMessage, getSqlStateAsString());
    }

    protected String buildErrorMessage(Object[] params) {
        String formatString = getMessageTemplate();
        return String.format(formatString, params);
    }

}
