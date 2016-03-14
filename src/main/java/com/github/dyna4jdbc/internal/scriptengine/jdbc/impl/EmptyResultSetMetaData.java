package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

class EmptyResultSetMetaData implements ResultSetMetaData {
    public int getColumnCount() throws SQLException {
        return 0;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public int isNullable(int column) throws SQLException {
        return DatabaseMetaData.columnNullableUnknown;
    }

    public boolean isSigned(int column) throws SQLException {
        return false;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }

    public String getColumnLabel(int column) throws SQLException {
        return null;
    }

    public String getColumnName(int column) throws SQLException {
        return "";
    }

    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    public int getScale(int column) throws SQLException {
        return 0;
    }

    public String getTableName(int column) throws SQLException {
        return "";
    }

    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    public int getColumnType(int column) throws SQLException {
        return Types.VARCHAR;
    }

    public String getColumnTypeName(int column) throws SQLException {
        return "VARCHAR";
    }

    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    public String getColumnClassName(int column) throws SQLException {
        return java.lang.String.class.getName();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
