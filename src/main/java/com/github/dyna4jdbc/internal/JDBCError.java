/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;

import com.github.dyna4jdbc.internal.sqlstate.SQLStateClass;


public enum JDBCError {

    // Warning class
    EXECUTION_ABORTED_AT_CLIENT_REQUEST("Execution aborted at client request",
            SQLStateClass.WARNING, "001"),

    // Error class DYNAMIC_SQL_ERROR
    SCRIPT_EXECUTION_EXCEPTION("Execution of script raised exception: %s",
            SQLStateClass.DYNAMIC_SQL_ERROR, "001"),
    LOADING_SCRIPTENGINE_FAILED("Could not load ScriptEngine '%s'",
            SQLStateClass.DYNAMIC_SQL_ERROR, "002"),
    INCONSISTENT_HEADER_SPECIFICATION("Inconsistent header specification: %s",
            SQLStateClass.DYNAMIC_SQL_ERROR, "003"),
    DUPLICATED_HEADER_NAME("Duplicated header name encountered: %s",
            SQLStateClass.DYNAMIC_SQL_ERROR, "004"),
    FORMAT_STRING_UNEXPECTED_FOR_COLUMN_TYPE("Format string '%s' unexpected for column type: %s",
            SQLStateClass.DYNAMIC_SQL_ERROR, "005"),
    FORMAT_STRING_INVALID("Format string '%s' is illegal: %s",
            SQLStateClass.DYNAMIC_SQL_ERROR, "006"),
    INVALID_FORMATTING_HEADER("Invalid formatting header detected in column %s: Value '%s' is invalid: %s",
            SQLStateClass.DYNAMIC_SQL_ERROR, "007"),
    RESULT_SET_MULTIPLE_EXPECTED_ONE("Expected one result set, but script produced %s result sets.",
            SQLStateClass.DYNAMIC_SQL_ERROR, "008"),


    // Error class ERROR_CONNECTION:
    CONNECT_FAILED_EXCEPTION("Failed to connect: %s (examine stack trace for details)",
            SQLStateClass.ERROR_CONNECTION, "001"),
    INITSCRIPT_READ_IO_ERROR("I/O error reading init script: %s",
            SQLStateClass.ERROR_CONNECTION, "100"),
    INITSCRIPT_EXECUTION_EXCEPTION("Exception executing init script: %s (examine stack trace for details)",
            SQLStateClass.ERROR_CONNECTION, "101"),
    REQUIRED_RESOURCE_UNAVAILABLE("A resource required to perform the operation could not be found: %s",
            SQLStateClass.ERROR_CONNECTION, "200"),


    // Error class FEATURE_NOT_SUPPORTED
    JDBC_FEATURE_NOT_SUPPORTED("This JDBC feature is not supported: %s",
            SQLStateClass.FEATURE_NOT_SUPPORTED, "001") {
        @Override
        public SQLException raiseSQLException(Object... params) throws SQLException {
            String errorMessage = buildErrorMessage(params);
            throw new SQLFeatureNotSupportedException(errorMessage, getSqlStateAsString());
        }
    },
    

    // Error class ERROR_DATA_EXCEPTION
    DATA_CONVERSION_FAILED("Data conversion failed in row %s, column %s. Value '%s' could not be converted to %s.",
            SQLStateClass.ERROR_DATA_EXCEPTION, "001"),

    // Error class EXTERNAL_ROUTINE_INVOCATION_EXCEPTION
    NON_STANDARD_COMPLIANT_SCRIPTENGINE("Non standard compliant ScriptEngine implementation: %s",
            SQLStateClass.EXTERNAL_ROUTINE_INVOCATION_EXCEPTION, "001"),
    NODE_JS_INTEGRATION_ERROR("Node.js integration error: %s",
            SQLStateClass.EXTERNAL_ROUTINE_INVOCATION_EXCEPTION, "002"),

    // Error class SYNTAX_OR_ACCESS_RULE_ERROR
    USING_STDOUT_FROM_UPDATE("Using standard output from an update call is not permitted",
            SQLStateClass.SYNTAX_OR_ACCESS_RULE_ERROR, "001"),


    // Error class CLIENT_ERROR
    INVALID_CONFIGURATION("Configuration error: %s",
            SQLStateClass.CLIENT_ERROR, "001"),
    JDBC_API_USAGE_CALLER_ERROR("Illegal JDBC API call: %s",
            SQLStateClass.CLIENT_ERROR, "002"),
    CANNOT_UNWARP_OBJECT("The requested type (%s) cannot be unwrapped from this object (%s).",
            SQLStateClass.CLIENT_ERROR, "003"),
    RESULT_SET_INDEX_ILLEGAL("Illegal index: %s",
            SQLStateClass.CLIENT_ERROR, "004"),
    CANCEL_REQUESTED_ALREADY("Cancellation requested already: %s",
            SQLStateClass.CLIENT_ERROR, "005"),


    // Error class SYSTEM_ERROR
    CLOSE_FAILED("Closing of '%s' caused error: %s",
            SQLStateClass.SYSTEM_ERROR, "001"),
    OBJECT_CLOSED("Object has already been closed: %s",
            SQLStateClass.SYSTEM_ERROR, "002"),
    CANCEL_FAILED("Failed to cancel the operation: %s",
            SQLStateClass.SYSTEM_ERROR, "003"),
    PARENT_CLOSE_TRIGGERED_FROM_CHILD_THREW_EXCEPTION("Closure of parent failed: %s",
            SQLStateClass.SYSTEM_ERROR, "004"),
    
    
    UNEXPECTED_THROWABLE("Processing failed; caught unexpected exception: %s",
            SQLStateClass.SYSTEM_ERROR, "900"),
    DRIVER_BUG_UNEXPECTED_STATE("An unexpected state has been reached: %s",
            SQLStateClass.SYSTEM_ERROR, "999");



    private static final int VENDOR_CODE_COMPLETION_FAILURE = 2;
    private final String message;
    private final String sqlStateCode;

    //CHECKSTYLE.OFF: VisibilityModifier
    final SQLStateClass sqlStateClass;
    //CHECKSTYLE.ON: VisibilityModifier

    JDBCError(String message, SQLStateClass sqlStateClass, String code) {
        this.message = message;
        this.sqlStateClass = sqlStateClass;
        this.sqlStateCode = String.format("%s%s", sqlStateClass.classCode, code);
    }

    @Override
    public String toString() {
        return String.format("JDBC Error [%s]", this.name());
    }

    protected String getMessageTemplate() {
        return String.format("[%s]: %s", this.name(), this.message);
    }

    public String getSqlStateAsString() {
        return sqlStateCode;
    }

    public SQLException raiseSQLException(Throwable throwable, Object... params) throws SQLException {
        String errorMessage = buildErrorMessage(params);
        throw new SQLException(errorMessage, getSqlStateAsString(), throwable);
    }

    public SQLException raiseSQLExceptionWithSuppressed(
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

    public RuntimeDyna4JdbcException raiseUncheckedExceptionWithSuppressed(
            Iterable<? extends Throwable> supressedThrowables,
            Object... params) throws RuntimeDyna4JdbcException {
        String errorMessage = buildErrorMessage(params);
        RuntimeDyna4JdbcException exception = new RuntimeDyna4JdbcException(errorMessage, getSqlStateAsString());
        for (Throwable supressed : supressedThrowables) {
            exception.addSuppressed(supressed);
        }
        throw exception;
    }

    protected String buildErrorMessage(Object[] params) {
        try {
            String formatString = getMessageTemplate();
            return String.format(formatString, params);
        } catch (java.util.IllegalFormatException formatException) {
            /*
             * we should have a sensible handling for the unlikely
             * case when the error message cannot be constructed
             * due to a bug in the arguments passed to from the
             * error handler routine
             */
            return String.format(
                     "Format string for %s is illegal! Failed to format from %s",
                     this, Arrays.toString(params));
        }
    }
}
