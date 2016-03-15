package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSetMetaData;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;

import java.sql.SQLException;
import java.util.List;


public class DataTableHolderResultSetMetaData extends AbstractResultSetMetaData {

    private final List<TypeHandler> typeHandlerList;

    public DataTableHolderResultSetMetaData(List<TypeHandler> typeHandlerList) {
        this.typeHandlerList = typeHandlerList;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return typeHandlerList.size();
    }

    private TypeHandler getTypeHandlerBySqlIndex(int sqlColumnIndex) throws SQLException {
        final int javaIndex = sqlColumnIndex - 1;

        if(javaIndex < 0 && javaIndex >= getColumnCount()) {
            throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException("Invalid column index: " + sqlColumnIndex);
        }

        return typeHandlerList.get(javaIndex);
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).isCurrency();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).isNullable();
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).isSigned();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getColumnDisplaySize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getColumnLabel();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getColumnName();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getScale();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getColumnType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getColumnTypeName();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return getTypeHandlerBySqlIndex(column).getColumnClass().getName();
    }
}
