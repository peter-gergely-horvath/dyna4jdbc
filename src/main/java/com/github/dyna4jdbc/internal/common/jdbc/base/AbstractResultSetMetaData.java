package com.github.dyna4jdbc.internal.common.jdbc.base;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.SQLError;

public abstract class AbstractResultSetMetaData implements ResultSetMetaData {

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return "";
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("unwrap of " + iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
