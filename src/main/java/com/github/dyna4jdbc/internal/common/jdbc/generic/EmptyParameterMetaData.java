package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractWrapper;

public final class EmptyParameterMetaData extends AbstractWrapper implements ParameterMetaData {

    private static final int NOT_APPLICABLE_OR_UNKNOWN = 0;
    
    @Override
    public int getParameterCount() throws SQLException {
        return NOT_APPLICABLE_OR_UNKNOWN;
    }

    @Override
    public int isNullable(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public boolean isSigned(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public int getPrecision(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public int getScale(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public int getParameterType(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public String getParameterTypeName(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public String getParameterClassName(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

    @Override
    public int getParameterMode(int param) throws SQLException {
        throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                "EmptyParameterMetaData set is empty!");
    }

}
