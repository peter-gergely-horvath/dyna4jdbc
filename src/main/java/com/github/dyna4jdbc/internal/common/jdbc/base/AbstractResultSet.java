/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.sqlwarning.SQLWarningContainer;

public abstract class AbstractResultSet<T> extends AbstractAutoCloseableJdbcObject implements ResultSet {

    private final Statement statement;

    private boolean wasNull = false;

    private final SQLWarningContainer warningContainer = new SQLWarningContainer();

    private int fetchSize;

    public AbstractResultSet(Statement statement) {
        super(statement);
        this.statement = statement;
    }

    @Override
    public final Statement getStatement() throws SQLException {
        checkNotClosed();

        return statement;
    }


    @Override
    public abstract int getRow() throws SQLException;

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        checkNotClosed();

        return this.warningContainer.getWarnings();
    }

    @Override
    public final void clearWarnings() throws SQLException {
        checkNotClosed();

        this.warningContainer.clearWarnings();
    }

    protected final void addSQLWarning(SQLWarning warning) {

        this.warningContainer.addSQLWarning(warning);
    }


    @Override
    public final void setFetchSize(int rows) throws SQLException {
        checkNotClosed();

        if (rows < 0) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Negative fetch size: " + rows);
        }
        this.fetchSize = rows;
    }


    @Override
    public final int getFetchSize() throws SQLException {
        checkNotClosed();

        return this.fetchSize;
    }



    @Override
    public final int getConcurrency() throws SQLException {
        checkNotClosed();

        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public final int getHoldability() throws SQLException {
        checkNotClosed();

        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public final RowId getRowId(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.RowId");
    }

    @Override
    public final RowId getRowId(String columnLabel) throws SQLException {
        checkNotClosed();

        return getRowId(findColumn(columnLabel));
    }

    @Override
    public final Ref getRef(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Ref");
    }

    @Override
    public final Ref getRef(String columnLabel) throws SQLException {
        checkNotClosed();

        return getRef(findColumn(columnLabel));
    }

    @Override
    public final Blob getBlob(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Blob");
    }

    @Override
    public final Clob getClob(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Clob");
    }

    @Override
    public final Array getArray(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Array");
    }

    @Override
    public final Blob getBlob(String columnLabel) throws SQLException {
        checkNotClosed();

        return getBlob(findColumn(columnLabel));
    }

    @Override
    public final Clob getClob(String columnLabel) throws SQLException {
        checkNotClosed();

        return getClob(findColumn(columnLabel));
    }

    @Override
    public final Array getArray(String columnLabel) throws SQLException {
        checkNotClosed();

        return getArray(findColumn(columnLabel));
    }

    @Override
    public final NClob getNClob(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.NClob");
    }

    @Override
    public final NClob getNClob(String columnLabel) throws SQLException {
        checkNotClosed();

        return getNClob(findColumn(columnLabel));
    }

    @Override
    public final SQLXML getSQLXML(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.SQLXML");
    }

    @Override
    public final SQLXML getSQLXML(String columnLabel) throws SQLException {
        checkNotClosed();

        return getSQLXML(findColumn(columnLabel));
    }

    @Override
    public final String getNString(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#getNString(int)");
    }

    @Override
    public final String getNString(String columnLabel) throws SQLException {
        checkNotClosed();

        return getNString(findColumn(columnLabel));
    }

    @Override
    public final Reader getNCharacterStream(int columnIndex) throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#getNCharacterStream(int)");
    }

    @Override
    public final Reader getNCharacterStream(String columnLabel) throws SQLException {
        checkNotClosed();

        return getNCharacterStream(findColumn(columnLabel));
    }


    @Override
    public final Object getObject(String columnLabel) throws SQLException {
        checkNotClosed();

        return getObject(findColumn(columnLabel));
    }

    @Override
    public final Reader getCharacterStream(String columnLabel) throws SQLException {
        checkNotClosed();

        return getCharacterStream(findColumn(columnLabel));
    }

    @Override
    public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        checkNotClosed();

        return getBigDecimal(findColumn(columnLabel));
    }

    @Override
    public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();

        return getObject(findColumn(columnLabel), map);
    }

    @Override
    public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
        checkNotClosed();

        return getDate(findColumn(columnLabel), cal);
    }

    @Override
    public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
        checkNotClosed();

        return getTime(findColumn(columnLabel), cal);
    }

    @Override
    public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        checkNotClosed();

        return getTimestamp(findColumn(columnLabel), cal);
    }

    @Override
    public final URL getURL(String columnLabel) throws SQLException {
        checkNotClosed();

        return getURL(findColumn(columnLabel));
    }

    @Override
    public final String getString(String columnLabel) throws SQLException {
        checkNotClosed();

        return getString(findColumn(columnLabel));
    }

    @Override
    public final boolean getBoolean(String columnLabel) throws SQLException {
        checkNotClosed();

        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public final byte getByte(String columnLabel) throws SQLException {
        checkNotClosed();

        return getByte(findColumn(columnLabel));
    }

    @Override
    public final short getShort(String columnLabel) throws SQLException {
        checkNotClosed();

        return getShort(findColumn(columnLabel));
    }

    @Override
    public final int getInt(String columnLabel) throws SQLException {
        checkNotClosed();

        return getInt(findColumn(columnLabel));
    }

    @Override
    public final long getLong(String columnLabel) throws SQLException {
        checkNotClosed();

        return getLong(findColumn(columnLabel));
    }

    @Override
    public final float getFloat(String columnLabel) throws SQLException {
        checkNotClosed();

        return getFloat(findColumn(columnLabel));
    }

    @Override
    public final double getDouble(String columnLabel) throws SQLException {
        checkNotClosed();

        return getDouble(findColumn(columnLabel));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        checkNotClosed();

        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public final byte[] getBytes(String columnLabel) throws SQLException {
        checkNotClosed();

        return getBytes(findColumn(columnLabel));
    }

    @Override
    public final Date getDate(String columnLabel) throws SQLException {
        checkNotClosed();

        return getDate(findColumn(columnLabel));
    }

    @Override
    public final Time getTime(String columnLabel) throws SQLException {
        checkNotClosed();

        return getTime(findColumn(columnLabel));
    }

    @Override
    public final Timestamp getTimestamp(String columnLabel) throws SQLException {
        checkNotClosed();

        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public final InputStream getAsciiStream(String columnLabel) throws SQLException {
        checkNotClosed();

        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
        checkNotClosed();

        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public final InputStream getBinaryStream(String columnLabel) throws SQLException {
        checkNotClosed();

        return getBinaryStream(findColumn(columnLabel));
    }

    protected final <R> R setWasNullBasedOnLastValue(R convertedValue) {
        wasNull = convertedValue == null;
        return convertedValue;
    }

    @Override
    public final boolean wasNull() throws SQLException {
        checkNotClosed();

        return wasNull;
    }

    @Override
    public final <R> R getObject(String columnLabel, Class<R> type) throws SQLException {
        checkNotClosed();

        return getObject(findColumn(columnLabel), type);
    }

    @Override
    public abstract int findColumn(String columnLabel) throws SQLException;

}
