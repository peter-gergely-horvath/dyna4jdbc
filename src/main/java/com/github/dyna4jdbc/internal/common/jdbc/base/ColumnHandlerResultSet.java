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
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * @author Peter G. Horvath
 */
abstract class ColumnHandlerResultSet<T> extends AbstractReadOnlyResultSet<T> {

    private final List<ColumnHandler> columnHandlers;
    private final Map<String, Integer> columnNameToColumnIndexMap;

    ColumnHandlerResultSet(Statement statement, List<ColumnHandler> columnHandlers) {
        super(statement);
        this.columnHandlers = columnHandlers;
        columnNameToColumnIndexMap = initColumnNameToColumnIndexMap(columnHandlers);
    }

    private static Map<String, Integer> initColumnNameToColumnIndexMap(List<ColumnHandler> columnColumnHandlers) {

        HashMap<String, Integer> columnNameToColumnIndexMap = new HashMap<>();

        int sqlIndex = 1;

        for (ColumnHandler columnHandler : columnColumnHandlers) {

            ColumnMetadata columnMetadata = columnHandler.getColumnMetadata();
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

    protected final List<ColumnHandler> getColumnHandlers() {
        return columnHandlers;
    }

    protected abstract String getRawCellValueBySqlColumnIndex(int sqlIndex) throws SQLException;

    private ColumnHandler getColumnHandlerBySqlIndex(int sqlIndex) throws SQLException {
        try {
            final int javaIndex = sqlIndex - 1;

            return columnHandlers.get(javaIndex);
        } catch (IndexOutOfBoundsException ex) {
            // NOTE: we report the sqlIndex coming from the caller!
            throw JDBCError.RESULT_SET_INDEX_ILLEGAL.raiseSQLException(sqlIndex);
        }
    }

    @Override
    public final int findColumn(String columnLabel) throws SQLException {
        checkNotClosed();

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
        checkNotClosed();
        
        return new ColumnHandlerResultSetMetaData(columnHandlers);
    }

    @Override
    public final String getString(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            String convertedValue = columnHandler.covertToString(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
                    String.class);
        }
    }

    //CHECKSTYLE.OFF: AvoidInlineConditionals
    @Override
    public final boolean getBoolean(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Boolean convertedValue = columnHandler.covertToBoolean(rawCellValue);
            Boolean returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : false;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "boolean");
        }
    }

    @Override
    public final byte getByte(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Byte convertedValue = columnHandler.covertToByte(rawCellValue);

            Byte returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : 0x0;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "byte");
        }
    }

    @Override
    public final short getShort(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Short convertedValue = columnHandler.covertToShort(rawCellValue);
            Short returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : 0;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "short");
        }
    }

    @Override
    public final int getInt(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Integer convertedValue = columnHandler.covertToInteger(rawCellValue);

            Integer returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : 0;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "int");
        }
    }

    @Override
    public final long getLong(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Long convertedValue = columnHandler.covertToLong(rawCellValue);

            Long returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : 0L;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "long");
        }
    }

    @Override
    public final float getFloat(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Float convertedValue = columnHandler.covertToFloat(rawCellValue);

            Float returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : 0.0f;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "float");
        }
    }

    @Override
    public final double getDouble(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Double convertedValue = columnHandler.covertToDouble(rawCellValue);

            Double returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : 0.0d;

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "double");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public final BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            BigDecimal convertedValue = columnHandler.covertToBigDecimal(rawCellValue, scale);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, BigDecimal.class);
        }
    }

    @Override
    public final byte[] getBytes(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            byte[] convertedValue = columnHandler.covertToByteArray(rawCellValue);

            byte[] returnValue = setWasNullBasedOnLastValue(convertedValue);

            return returnValue != null ? returnValue : new byte[0];

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "byte[]");
        }
    }

    @Override
    public final Date getDate(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Date convertedValue = columnHandler.covertToDate(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Date.class);
        }
    }

    @Override
    public final Time getTime(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Time convertedValue = columnHandler.covertToTime(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Time.class);
        }
    }

    @Override
    public final Timestamp getTimestamp(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Timestamp convertedValue = columnHandler.covertToTimestamp(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Timestamp.class);
        }
    }

    @Override
    public final InputStream getAsciiStream(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            InputStream convertedValue = columnHandler.covertToAsciiInputStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "(ASCII) InputStream");
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public final InputStream getUnicodeStream(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            InputStream convertedValue = columnHandler.covertToUnicodeInputStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "(Unicode) InputStream");
        }
    }

    @Override
    public final InputStream getBinaryStream(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            InputStream convertedValue = columnHandler.covertToBinaryInputStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, "(Binary) InputStream");
        }
    }

    @Override
    public final Object getObject(int columnIndex) throws SQLException {
        checkNotClosed();

        return getObject(columnIndex, (Map<String, Class<?>>) null);
    }

    @Override
    public final Reader getCharacterStream(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Reader convertedValue = columnHandler.covertToCharacterStream(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Reader.class);
        }
    }

    @Override
    public final BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            BigDecimal convertedValue = columnHandler.covertToBigDecimal(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Reader.class);
        }
    }

    @Override
    public final Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Object convertedValue = columnHandler.covertToObject(rawCellValue, map);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Object.class);
        }
    }

    @Override
    public final Date getDate(int columnIndex, Calendar cal) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Date convertedValue = columnHandler.covertToDate(rawCellValue, cal);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Date.class);
        }
    }

    @Override
    public final Time getTime(int columnIndex, Calendar cal) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Time convertedValue = columnHandler.covertToTime(rawCellValue, cal);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Time.class);
        }
    }

    @Override
    public final Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            Timestamp convertedValue = columnHandler.covertToTimestamp(rawCellValue, cal);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, Timestamp.class);
        }
    }

    @Override
    public final URL getURL(int columnIndex) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            URL convertedValue = columnHandler.covertToURL(rawCellValue);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, URL.class);
        }
    }

    @Override
    public final <R> R getObject(int columnIndex, Class<R> type) throws SQLException {
        checkNotClosed();

        String rawCellValue = getRawCellValueBySqlColumnIndex(columnIndex);

        try {
            ColumnHandler columnHandler = getColumnHandlerBySqlIndex(columnIndex);
            R convertedValue = columnHandler.covertToObject(rawCellValue, type);
            return setWasNullBasedOnLastValue(convertedValue);

        } catch (TypeConversionException tce) {
            throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
                    tce, getRow(), columnIndex, rawCellValue, type);
        }
    }
    //CHECKSTYLE.ON: AvoidInlineConditionals
}
