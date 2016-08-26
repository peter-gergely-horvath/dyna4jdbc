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
        return statement;
    }


    @Override
    public abstract int getRow() throws SQLException;

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        return this.warningContainer.getWarnings();
    }

    @Override
    public final void clearWarnings() throws SQLException {
        this.warningContainer.clearWarnings();
    }

    protected final void addSQLWarning(SQLWarning warning) {

        this.warningContainer.addSQLWarning(warning);
    }


    @Override
    public final void setFetchSize(int rows) throws SQLException {
        if (rows < 0) {
            JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Negative fetch size: " + rows);
        }
        this.fetchSize = rows;
    }


    @Override
    public final int getFetchSize() throws SQLException {
        return this.fetchSize;
    }



    @Override
    public final int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public final int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public final RowId getRowId(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.RowId");
    }

    @Override
    public final RowId getRowId(String columnLabel) throws SQLException {
        return getRowId(findColumn(columnLabel));
    }

    @Override
    public final Ref getRef(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Ref");
    }

    @Override
    public final Ref getRef(String columnLabel) throws SQLException {
        return getRef(findColumn(columnLabel));
    }

    @Override
    public final Blob getBlob(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Blob");
    }

    @Override
    public final Clob getClob(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Clob");
    }

    @Override
    public final Array getArray(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.Array");
    }

    @Override
    public final Blob getBlob(String columnLabel) throws SQLException {
        return getBlob(findColumn(columnLabel));
    }

    @Override
    public final Clob getClob(String columnLabel) throws SQLException {
        return getClob(findColumn(columnLabel));
    }

    @Override
    public final Array getArray(String columnLabel) throws SQLException {
        return getArray(findColumn(columnLabel));
    }

    @Override
    public final NClob getNClob(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.NClob");
    }

    @Override
    public final NClob getNClob(String columnLabel) throws SQLException {
        return getNClob(findColumn(columnLabel));
    }

    @Override
    public final SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.SQLXML");
    }

    @Override
    public final SQLXML getSQLXML(String columnLabel) throws SQLException {
        return getSQLXML(findColumn(columnLabel));
    }

    @Override
    public final String getNString(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#getNString(int)");
    }

    @Override
    public final String getNString(String columnLabel) throws SQLException {
        return getNString(findColumn(columnLabel));
    }

    @Override
    public final Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#getNCharacterStream(int)");
    }

    @Override
    public final Reader getNCharacterStream(String columnLabel) throws SQLException {
        return getNCharacterStream(findColumn(columnLabel));
    }


    @Override
    public final Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public final Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    @Override
    public final BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    @Override
    public final Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    @Override
    public final Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    @Override
    public final Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel), cal);
    }

    @Override
    public final Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    @Override
    public final URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    @Override
    public final String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public final boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public final byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public final short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public final int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public final long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public final float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public final double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public final byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public final Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public final Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public final Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public final InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public final InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    protected final <R> R setWasNullBasedOnLastValue(R convertedValue) {
        wasNull = (convertedValue == null);
        return convertedValue;
    }

    @Override
    public final boolean wasNull() throws SQLException {
        return wasNull;
    }

    @Override
    public final <R> R getObject(String columnLabel, Class<R> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }

    @Override
    public abstract int findColumn(String columnLabel) throws SQLException;

}
