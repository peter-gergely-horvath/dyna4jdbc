package com.github.dyna4jdbc.internal.connection.scriptengine;

import com.github.dyna4jdbc.internal.ClosableSQLObject;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

class EmptyResultSet extends ClosableSQLObject implements ResultSet{


    public boolean next() throws SQLException {
        return false;
    }

    public boolean wasNull() throws SQLException {
        return true;
    }

    public String getString(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getString"); // TODO: implement method
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getBoolean"); // TODO: implement method
    }

    public byte getByte(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getByte"); // TODO: implement method
    }

    public short getShort(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getShort"); // TODO: implement method
    }

    public int getInt(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getInt"); // TODO: implement method
    }

    public long getLong(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getLong"); // TODO: implement method
    }

    public float getFloat(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getFloat"); // TODO: implement method
    }

    public double getDouble(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getDouble"); // TODO: implement method
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        throw new SQLException("EmptyResultSet#getBigDecimal"); // TODO: implement method
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getBytes"); // TODO: implement method
    }

    public Date getDate(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getDate"); // TODO: implement method
    }

    public Time getTime(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getTime"); // TODO: implement method
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getTimestamp"); // TODO: implement method
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getAsciiStream"); // TODO: implement method
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getUnicodeStream"); // TODO: implement method
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getBinaryStream"); // TODO: implement method
    }

    public String getString(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getString"); // TODO: implement method
    }

    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getBoolean"); // TODO: implement method
    }

    public byte getByte(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getByte"); // TODO: implement method
    }

    public short getShort(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getShort"); // TODO: implement method
    }

    public int getInt(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getInt"); // TODO: implement method
    }

    public long getLong(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getLong"); // TODO: implement method
    }

    public float getFloat(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getFloat"); // TODO: implement method
    }

    public double getDouble(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getDouble"); // TODO: implement method
    }

    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new SQLException("EmptyResultSet#getBigDecimal"); // TODO: implement method
    }

    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getBytes"); // TODO: implement method
    }

    public Date getDate(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getDate"); // TODO: implement method
    }

    public Time getTime(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getTime"); // TODO: implement method
    }

    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getTimestamp"); // TODO: implement method
    }

    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getAsciiStream"); // TODO: implement method
    }

    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getUnicodeStream"); // TODO: implement method
    }

    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getBinaryStream"); // TODO: implement method
    }

    public SQLWarning getWarnings() throws SQLException {
        throw new SQLException("EmptyResultSet#getWarnings"); // TODO: implement method
    }

    public void clearWarnings() throws SQLException {
        throw new SQLException("EmptyResultSet#clearWarnings"); // TODO: implement method
    }

    public String getCursorName() throws SQLException {
        throw new SQLException("EmptyResultSet#getCursorName"); // TODO: implement method
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return new EmptyResultSetMetaData();
    }

    public Object getObject(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getObject"); // TODO: implement method
    }

    public Object getObject(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getObject"); // TODO: implement method
    }

    public int findColumn(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#findColumn"); // TODO: implement method
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getCharacterStream"); // TODO: implement method
    }

    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getCharacterStream"); // TODO: implement method
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getBigDecimal"); // TODO: implement method
    }

    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getBigDecimal"); // TODO: implement method
    }

    public boolean isBeforeFirst() throws SQLException {
        throw new SQLException("EmptyResultSet#isBeforeFirst"); // TODO: implement method
    }

    public boolean isAfterLast() throws SQLException {
        throw new SQLException("EmptyResultSet#isAfterLast"); // TODO: implement method
    }

    public boolean isFirst() throws SQLException {
        throw new SQLException("EmptyResultSet#isFirst"); // TODO: implement method
    }

    public boolean isLast() throws SQLException {
        throw new SQLException("EmptyResultSet#isLast"); // TODO: implement method
    }

    public void beforeFirst() throws SQLException {
        throw new SQLException("EmptyResultSet#beforeFirst"); // TODO: implement method
    }

    public void afterLast() throws SQLException {
        throw new SQLException("EmptyResultSet#afterLast"); // TODO: implement method
    }

    public boolean first() throws SQLException {
        throw new SQLException("EmptyResultSet#first"); // TODO: implement method
    }

    public boolean last() throws SQLException {
        throw new SQLException("EmptyResultSet#last"); // TODO: implement method
    }

    public int getRow() throws SQLException {
        throw new SQLException("EmptyResultSet#getRow"); // TODO: implement method
    }

    public boolean absolute(int row) throws SQLException {
        throw new SQLException("EmptyResultSet#absolute"); // TODO: implement method
    }

    public boolean relative(int rows) throws SQLException {
        throw new SQLException("EmptyResultSet#relative"); // TODO: implement method
    }

    public boolean previous() throws SQLException {
        throw new SQLException("EmptyResultSet#previous"); // TODO: implement method
    }

    public void setFetchDirection(int direction) throws SQLException {
        throw new SQLException("EmptyResultSet#setFetchDirection"); // TODO: implement method
    }

    public int getFetchDirection() throws SQLException {
        throw new SQLException("EmptyResultSet#getFetchDirection"); // TODO: implement method
    }

    public void setFetchSize(int rows) throws SQLException {
        throw new SQLException("EmptyResultSet#setFetchSize"); // TODO: implement method
    }

    public int getFetchSize() throws SQLException {
        throw new SQLException("EmptyResultSet#getFetchSize"); // TODO: implement method
    }

    public int getType() throws SQLException {
        throw new SQLException("EmptyResultSet#getType"); // TODO: implement method
    }

    public int getConcurrency() throws SQLException {
        throw new SQLException("EmptyResultSet#getConcurrency"); // TODO: implement method
    }

    public boolean rowUpdated() throws SQLException {
        throw new SQLException("EmptyResultSet#rowUpdated"); // TODO: implement method
    }

    public boolean rowInserted() throws SQLException {
        throw new SQLException("EmptyResultSet#rowInserted"); // TODO: implement method
    }

    public boolean rowDeleted() throws SQLException {
        throw new SQLException("EmptyResultSet#rowDeleted"); // TODO: implement method
    }

    public void updateNull(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNull"); // TODO: implement method
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBoolean"); // TODO: implement method
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateByte"); // TODO: implement method
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateShort"); // TODO: implement method
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateInt"); // TODO: implement method
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateLong"); // TODO: implement method
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateFloat"); // TODO: implement method
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateDouble"); // TODO: implement method
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBigDecimal"); // TODO: implement method
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateString"); // TODO: implement method
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBytes"); // TODO: implement method
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateDate"); // TODO: implement method
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateTime"); // TODO: implement method
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateTimestamp"); // TODO: implement method
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateAsciiStream"); // TODO: implement method
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBinaryStream"); // TODO: implement method
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateCharacterStream"); // TODO: implement method
    }

    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("EmptyResultSet#updateObject"); // TODO: implement method
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateObject"); // TODO: implement method
    }

    public void updateNull(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNull"); // TODO: implement method
    }

    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBoolean"); // TODO: implement method
    }

    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateByte"); // TODO: implement method
    }

    public void updateShort(String columnLabel, short x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateShort"); // TODO: implement method
    }

    public void updateInt(String columnLabel, int x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateInt"); // TODO: implement method
    }

    public void updateLong(String columnLabel, long x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateLong"); // TODO: implement method
    }

    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateFloat"); // TODO: implement method
    }

    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateDouble"); // TODO: implement method
    }

    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBigDecimal"); // TODO: implement method
    }

    public void updateString(String columnLabel, String x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateString"); // TODO: implement method
    }

    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBytes"); // TODO: implement method
    }

    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateDate"); // TODO: implement method
    }

    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateTime"); // TODO: implement method
    }

    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateTimestamp"); // TODO: implement method
    }

    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateAsciiStream"); // TODO: implement method
    }

    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBinaryStream"); // TODO: implement method
    }

    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateCharacterStream"); // TODO: implement method
    }

    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw new SQLException("EmptyResultSet#updateObject"); // TODO: implement method
    }

    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateObject"); // TODO: implement method
    }

    public void insertRow() throws SQLException {
        throw new SQLException("EmptyResultSet#insertRow"); // TODO: implement method
    }

    public void updateRow() throws SQLException {
        throw new SQLException("EmptyResultSet#updateRow"); // TODO: implement method
    }

    public void deleteRow() throws SQLException {
        throw new SQLException("EmptyResultSet#deleteRow"); // TODO: implement method
    }

    public void refreshRow() throws SQLException {
        throw new SQLException("EmptyResultSet#refreshRow"); // TODO: implement method
    }

    public void cancelRowUpdates() throws SQLException {
        throw new SQLException("EmptyResultSet#cancelRowUpdates"); // TODO: implement method
    }

    public void moveToInsertRow() throws SQLException {
        throw new SQLException("EmptyResultSet#moveToInsertRow"); // TODO: implement method
    }

    public void moveToCurrentRow() throws SQLException {
        throw new SQLException("EmptyResultSet#moveToCurrentRow"); // TODO: implement method
    }

    public Statement getStatement() throws SQLException {
        throw new SQLException("EmptyResultSet#getStatement"); // TODO: implement method
    }

    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("EmptyResultSet#getObject"); // TODO: implement method
    }

    public Ref getRef(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getRef"); // TODO: implement method
    }

    public Blob getBlob(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getBlob"); // TODO: implement method
    }

    public Clob getClob(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getClob"); // TODO: implement method
    }

    public Array getArray(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getArray"); // TODO: implement method
    }

    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new SQLException("EmptyResultSet#getObject"); // TODO: implement method
    }

    public Ref getRef(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getRef"); // TODO: implement method
    }

    public Blob getBlob(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getBlob"); // TODO: implement method
    }

    public Clob getClob(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getClob"); // TODO: implement method
    }

    public Array getArray(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getArray"); // TODO: implement method
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("EmptyResultSet#getDate"); // TODO: implement method
    }

    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException("EmptyResultSet#getDate"); // TODO: implement method
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("EmptyResultSet#getTime"); // TODO: implement method
    }

    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException("EmptyResultSet#getTime"); // TODO: implement method
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new SQLException("EmptyResultSet#getTimestamp"); // TODO: implement method
    }

    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new SQLException("EmptyResultSet#getTimestamp"); // TODO: implement method
    }

    public URL getURL(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getURL"); // TODO: implement method
    }

    public URL getURL(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getURL"); // TODO: implement method
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateRef"); // TODO: implement method
    }

    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateRef"); // TODO: implement method
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBlob"); // TODO: implement method
    }

    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBlob"); // TODO: implement method
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateClob"); // TODO: implement method
    }

    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateClob"); // TODO: implement method
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateArray"); // TODO: implement method
    }

    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateArray"); // TODO: implement method
    }

    public RowId getRowId(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getRowId"); // TODO: implement method
    }

    public RowId getRowId(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getRowId"); // TODO: implement method
    }

    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateRowId"); // TODO: implement method
    }

    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateRowId"); // TODO: implement method
    }

    public int getHoldability() throws SQLException {
        throw new SQLException("EmptyResultSet#getHoldability"); // TODO: implement method
    }

    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNString"); // TODO: implement method
    }

    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNString"); // TODO: implement method
    }

    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNClob"); // TODO: implement method
    }

    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNClob"); // TODO: implement method
    }

    public NClob getNClob(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getNClob"); // TODO: implement method
    }

    public NClob getNClob(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getNClob"); // TODO: implement method
    }

    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getSQLXML"); // TODO: implement method
    }

    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getSQLXML"); // TODO: implement method
    }

    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new SQLException("EmptyResultSet#updateSQLXML"); // TODO: implement method
    }

    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new SQLException("EmptyResultSet#updateSQLXML"); // TODO: implement method
    }

    public String getNString(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getNString"); // TODO: implement method
    }

    public String getNString(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getNString"); // TODO: implement method
    }

    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new SQLException("EmptyResultSet#getNCharacterStream"); // TODO: implement method
    }

    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new SQLException("EmptyResultSet#getNCharacterStream"); // TODO: implement method
    }

    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNCharacterStream"); // TODO: implement method
    }

    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNCharacterStream"); // TODO: implement method
    }

    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateAsciiStream"); // TODO: implement method
    }

    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBinaryStream"); // TODO: implement method
    }

    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateCharacterStream"); // TODO: implement method
    }

    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateAsciiStream"); // TODO: implement method
    }

    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBinaryStream"); // TODO: implement method
    }

    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateCharacterStream"); // TODO: implement method
    }

    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBlob"); // TODO: implement method
    }

    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBlob"); // TODO: implement method
    }

    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateClob"); // TODO: implement method
    }

    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateClob"); // TODO: implement method
    }

    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNClob"); // TODO: implement method
    }

    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNClob"); // TODO: implement method
    }

    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNCharacterStream"); // TODO: implement method
    }

    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNCharacterStream"); // TODO: implement method
    }

    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateAsciiStream"); // TODO: implement method
    }

    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBinaryStream"); // TODO: implement method
    }

    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateCharacterStream"); // TODO: implement method
    }

    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateAsciiStream"); // TODO: implement method
    }

    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBinaryStream"); // TODO: implement method
    }

    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("EmptyResultSet#updateCharacterStream"); // TODO: implement method
    }

    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBlob"); // TODO: implement method
    }

    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw new SQLException("EmptyResultSet#updateBlob"); // TODO: implement method
    }

    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException("EmptyResultSet#updateClob"); // TODO: implement method
    }

    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("EmptyResultSet#updateClob"); // TODO: implement method
    }

    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNClob"); // TODO: implement method
    }

    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw new SQLException("EmptyResultSet#updateNClob"); // TODO: implement method
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLException("EmptyResultSet#getObject"); // TODO: implement method
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLException("EmptyResultSet#getObject"); // TODO: implement method
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("EmptyResultSet#unwrap"); // TODO: implement method
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("EmptyResultSet#isWrapperFor"); // TODO: implement method
    }


}
