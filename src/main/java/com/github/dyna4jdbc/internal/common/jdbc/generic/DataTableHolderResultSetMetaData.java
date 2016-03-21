package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSetMetaData;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
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

    private ColumnMetadata getColumnMetadataBySqlIndex(int sqlColumnIndex) throws SQLException {
        final int javaIndex = sqlColumnIndex - 1;

        if(javaIndex < 0 || javaIndex >= getColumnCount()) {
            throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException("Invalid column index: " + sqlColumnIndex);
        }

        TypeHandler typeHandler = typeHandlerList.get(javaIndex);
        
        ColumnMetadata columnMetadata = typeHandler.getColumnMetadata();
        if(columnMetadata == null) {
        	throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException("columnMetadata is null");
        }
        
        return columnMetadata;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).isCurrency();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).isNullable();
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).isSigned();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getColumnDisplaySize();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getColumnLabel();
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getColumnName();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getPrecision();
    }

    @Override
    public int getScale(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getScale();
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getColumnType();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getColumnTypeName();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).getColumnClass().getName();
    }
}
