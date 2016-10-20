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

 
package com.github.dyna4jdbc.internal.common.jdbc.base;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.SQLDataType;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public final class ColumnHandlerResultSetMetaData extends AbstractResultSetMetaData {


    private final List<ColumnHandler> columnHandlerList;

    public ColumnHandlerResultSetMetaData(List<ColumnHandler> columnHandlerList) {
        this.columnHandlerList = columnHandlerList;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnHandlerList.size();
    }

    private ColumnMetadata getColumnMetadataBySqlIndex(int sqlColumnIndex) throws SQLException {
        final int javaIndex = sqlColumnIndex - 1;

        if (javaIndex < 0 || javaIndex >= getColumnCount()) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Invalid column index: " + sqlColumnIndex);
        }

        ColumnHandler columnHandler = columnHandlerList.get(javaIndex);

        ColumnMetadata columnMetadata = columnHandler.getColumnMetadata();
        if (columnMetadata == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException("columnMetadata is null");
        }

        return columnMetadata;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return getColumnMetadataBySqlIndex(column).isCurrency();
    }

    @Override
    public int isNullable(int column) throws SQLException {

        // adapt our non-JDBC specific Nullability to JDBC API values
        Nullability nullability = getColumnMetadataBySqlIndex(column).getNullability();
        if (nullability == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                    "Column Nullability indicator is null.");
        }

        switch (nullability) {
            case NOT_NULLABLE:
                return ResultSetMetaData.columnNoNulls;

            case NULLABLE:
                return ResultSetMetaData.columnNullable;

            case UNKNOWN:
                return ResultSetMetaData.columnNullableUnknown;

            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                        "Unknown Nullability value: " + nullability);
        }
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
        return getColumnMetadataBySqlIndex(column).getColumnType().javaSqlTypesInt;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        SQLDataType columnType = getColumnMetadataBySqlIndex(column).getColumnType();
        if (columnType == null) {
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                        "columnType is null");
        }

        return columnType.name;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        SQLDataType columnType = getColumnMetadataBySqlIndex(column).getColumnType();
        if (columnType == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                    "columnType is null");
        }
        
        String columnClassName;
        if (columnType.mappingClass == null) {
            columnClassName = null;
        } else {
            columnClassName = columnType.mappingClass.getName();
        }
        
        return columnClassName;
    }
}
