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

package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractReadOnlyResultSet;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * @author Peter Horvath
 */
public abstract class TypeHandlerResultSet extends AbstractReadOnlyResultSet<List<String>> {

    protected final List<TypeHandler> typeHandlers;
    protected final Map<String, Integer> columnNameToColumnIndexMap;

    public TypeHandlerResultSet(
            Iterable<List<String>> dataRowIterable,
            Statement statement, List<TypeHandler> typeHandlers) {

        super(dataRowIterable, statement);
        this.typeHandlers = typeHandlers;
        columnNameToColumnIndexMap = initColumnNameToColumnIndexMap(typeHandlers);
    }

    private static Map<String, Integer> initColumnNameToColumnIndexMap(List<TypeHandler> columnTypeHandlers) {

        HashMap<String, Integer> columnNameToColumnIndexMap = new HashMap<>();

        int sqlIndex = 1;

        for (TypeHandler typeHandler : columnTypeHandlers) {

            ColumnMetadata columnMetadata = typeHandler.getColumnMetadata();
            if (columnMetadata == null) {
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("columnMetadata is null");
            }

            String columnLabel = columnMetadata.getColumnLabel();
            if (columnNameToColumnIndexMap.containsKey(columnLabel)) {
                throw JDBCError.DUPLICATED_HEADER_NAME.raiseUncheckedException(columnLabel);
            }

            columnNameToColumnIndexMap.put(columnLabel, sqlIndex);

            sqlIndex++;
        }

        return Collections.unmodifiableMap(columnNameToColumnIndexMap);
    }

    protected abstract String getRawCellValueBySqlColumnIndex(int sqlIndex) throws SQLException;

    private TypeHandler getTypeHandlerByBySqlIndex(int sqlIndex) throws SQLException {
        final int javaIndex = sqlIndex - 1;

        TypeHandler typeHandler = typeHandlers.get(javaIndex);

        return typeHandler;
    }

    @Override
    public final int findColumn(String columnLabel) throws SQLException {
        if (!columnNameToColumnIndexMap.containsKey(columnLabel)) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Invalid column label: " + columnLabel);
        }

        Integer sqlIndex = columnNameToColumnIndexMap.get(columnLabel);
        if (sqlIndex == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException("sqlIndex is null");
        }

        return sqlIndex;
    }

    @Override
    public final ResultSetMetaData getMetaData() throws SQLException {
        return new TypeHandlerResultSetMetaData(typeHandlers);
    }

    @Override
    public final String getString(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            String convertedValue = typeHandler.covertToString(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
                    String.class);
        }
    }

    @Override
    public final boolean getBoolean(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Boolean convertedValue = typeHandler.covertToBoolean(rawCellValue);
            Boolean returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.booleanValue() : false;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "boolean");
        }
    }

    @Override
    public final byte getByte(int columnIndex) throws SQLException {

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Byte convertedValue = typeHandler.covertToByte(rawCellValue);

            Byte returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.byteValue() : 0x0;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "byte");
        }
    }

    @Override
    public final short getShort(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Short convertedValue = typeHandler.covertToShort(rawCellValue);
            Short returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.shortValue() : 0;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "short");
        }
    }

    @Override
    public final int getInt(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Integer convertedValue = typeHandler.covertToInteger(rawCellValue);

            Integer returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.intValue() : 0;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "int");
        }
    }

    @Override
    public final long getLong(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Long convertedValue = typeHandler.covertToLong(rawCellValue);

            Long returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.longValue() : 0L;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "long");
        }
    }

    @Override
    public final float getFloat(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Float convertedValue = typeHandler.covertToFloat(rawCellValue);

            Float returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.floatValue() : 0.0f;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "float");
        }
    }

    @Override
    public final double getDouble(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Double convertedValue = typeHandler.covertToDouble(rawCellValue);

            Double returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue.doubleValue() : 0.0d;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "double");
        }
    }

    @Override
    public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            BigDecimal convertedValue = typeHandler.covertToBigDecimal(rawCellValue, scale);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, BigDecimal.class);
        }
    }

    @Override
    public final byte[] getBytes(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            byte[] convertedValue = typeHandler.covertToByteArray(rawCellValue);

            byte[] returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : new byte[0];

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "byte[]");
        }
    }

    @Override
    public final Date getDate(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Date convertedValue = typeHandler.covertToDate(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Date.class);
        }
    }

    @Override
    public final Time getTime(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Time convertedValue = typeHandler.covertToTime(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Time.class);
        }
    }

    @Override
    public final Timestamp getTimestamp(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Timestamp convertedValue = typeHandler.covertToTimestamp(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Timestamp.class);
        }
    }

    @Override
    public final InputStream getAsciiStream(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            InputStream convertedValue = typeHandler.covertToAsciiInputStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "(ASCII) InputStream");
        }
    }

    @Override
    public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            InputStream convertedValue = typeHandler.covertToUnicodeInputStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "(Unicode) InputStream");
        }
    }

    @Override
    public final InputStream getBinaryStream(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            InputStream convertedValue = typeHandler.covertToBinaryInputStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "(Binary) InputStream");
        }
    }

    @Override
    public final Object getObject(int columnIndex) throws SQLException {
        return getObject(columnIndex, (Map<String, Class<?>>) null);
    }

    @Override
    public final Reader getCharacterStream(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Reader convertedValue = typeHandler.covertToCharacterStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Reader.class);
        }
    }

    @Override
    public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            BigDecimal convertedValue = typeHandler.covertToBigDecimal(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Reader.class);
        }
    }

    @Override
    public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Object convertedValue = typeHandler.covertToObject(rawCellValue, map);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Object.class);
        }
    }

    @Override
    public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Date convertedValue = typeHandler.covertToDate(rawCellValue, cal);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Date.class);
        }
    }

    @Override
    public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Time convertedValue = typeHandler.covertToTime(rawCellValue, cal);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Time.class);
        }
    }

    @Override
    public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            Timestamp convertedValue = typeHandler.covertToTimestamp(rawCellValue, cal);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Timestamp.class);
        }
    }

    @Override
    public final URL getURL(int columnIndex) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            URL convertedValue = typeHandler.covertToURL(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, URL.class);
        }
    }

    @Override
    public final <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
            T convertedValue = typeHandler.covertToObject(rawCellValue, type);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, type);
        }
    }
}
