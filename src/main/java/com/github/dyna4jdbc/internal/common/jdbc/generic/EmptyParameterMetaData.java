package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractWrapper;

final class EmptyParameterMetaData extends AbstractWrapper implements java.sql.ParameterMetaData {

    private static final int ZERO_PARAMETER_COUNT = 0;
    private static final String PARAMETER_METADATA_NOT_AVAILABLE = "Parameter meta data is not available";

    @Override
    public int getParameterCount() throws SQLException {
        return ZERO_PARAMETER_COUNT;
    }

    @Override
    public int isNullable(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getScale(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(PARAMETER_METADATA_NOT_AVAILABLE);
    }

}
