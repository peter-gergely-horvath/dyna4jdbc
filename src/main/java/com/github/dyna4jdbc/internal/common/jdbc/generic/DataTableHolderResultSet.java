package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSet;

public class DataTableHolderResultSet extends AbstractResultSet implements ResultSet {

    private final DataTable dataTable;
    private final Iterator<DataTable.Row> rowIterator;
    private DataTable.Row currentRow = null;
    private boolean wasNull = false;

    private GuardedResultSetState resultSetState = new GuardedResultSetState();

    public DataTableHolderResultSet(DataTable dataTable) {
        this.dataTable = dataTable;
        this.rowIterator = dataTable.iterator();
    }


    public boolean next() throws SQLException {
        checkNotClosed();

        GuardedResultSetState.State currentState = resultSetState.getCurrentState();
        switch (currentState) {
            case BEFORE_FIRST: {
                if(rowIterator.hasNext()) {
                    resultSetState.transitionTo(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
                    currentRow = rowIterator.next();
                } else {
                    resultSetState.transitionTo(GuardedResultSetState.State.AFTER_LAST);
                }

                return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
            }



            case ITERATING_OVER_RESULTS: {
                if(rowIterator.hasNext()) {
                    currentRow = rowIterator.next();
                } else {
                    resultSetState.transitionTo(GuardedResultSetState.State.AFTER_LAST);
                }

                return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
            }

            case AFTER_LAST: {
                throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException("Calling next() in state " + currentState);
            }



            default:
                throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException("Unexpected currentState: " + currentState);
        }
    }


    private void checkResultSetState() throws SQLException {
        checkNotClosed();
        resultSetState.checkValidStateForRowAccess();
    }

    @Override
    public void close() throws SQLException {
        checkNotClosed();

        dataTable.clear();

        super.close();
    }

    private String getCellValueBySqlIndex(int sqlIndex) throws SQLException {
        checkResultSetState();

        final int javaIndex = sqlIndex - 1;


        if(currentRow == null) {
            throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException(
                    "currentRow is null in state: " + resultSetState);
        }

        if(! currentRow.isValidIndex(javaIndex)) {
            throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException(
                    "Invalid index: " + sqlIndex);
        }

        DataTable.Row.Cell cell = currentRow.getCell(javaIndex);
        if(cell == null) {
            throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException(
                    "Indexed cell not found: " + javaIndex);
        }

        String cellValue = cell.getValue();
        wasNull = cellValue == null;

        return cellValue;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return wasNull;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return getCellValueBySqlIndex(columnIndex);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return Boolean.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return Byte.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return Short.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return Integer.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return Long.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return Long.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return Double.valueOf(getCellValueBySqlIndex(columnIndex));
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return BigDecimal.valueOf(getLong(columnIndex), scale);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return getCellValueBySqlIndex(columnIndex).getBytes();
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getAsciiStream"); // TODO: implement method
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getUnicodeStream"); // TODO: implement method
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBinaryStream"); // TODO: implement method
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getString"); // TODO: implement method
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBoolean"); // TODO: implement method
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getByte"); // TODO: implement method
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getShort"); // TODO: implement method
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getInt"); // TODO: implement method
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getLong"); // TODO: implement method
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getFloat"); // TODO: implement method
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDouble"); // TODO: implement method
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBigDecimal"); // TODO: implement method
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBytes"); // TODO: implement method
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getAsciiStream"); // TODO: implement method
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getUnicodeStream"); // TODO: implement method
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBinaryStream"); // TODO: implement method
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getCursorName"); // TODO: implement method
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getMetaData"); // TODO: implement method
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#findColumn"); // TODO: implement method
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getCharacterStream"); // TODO: implement method
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getCharacterStream"); // TODO: implement method
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBigDecimal"); // TODO: implement method
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBigDecimal"); // TODO: implement method
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#isBeforeFirst"); // TODO: implement method
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#isAfterLast"); // TODO: implement method
    }

    @Override
    public boolean isFirst() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#isFirst"); // TODO: implement method
    }

    @Override
    public boolean isLast() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#isLast"); // TODO: implement method
    }

    @Override
    public void beforeFirst() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#beforeFirst"); // TODO: implement method
    }

    @Override
    public void afterLast() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#afterLast"); // TODO: implement method
    }

    @Override
    public boolean first() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#first"); // TODO: implement method
    }

    @Override
    public boolean last() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#last"); // TODO: implement method
    }

    @Override
    public int getRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRow"); // TODO: implement method
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#absolute"); // TODO: implement method
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#relative"); // TODO: implement method
    }

    @Override
    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#previous"); // TODO: implement method
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#setFetchDirection"); // TODO: implement method
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getFetchDirection"); // TODO: implement method
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#setFetchSize"); // TODO: implement method
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getFetchSize"); // TODO: implement method
    }

    @Override
    public int getType() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getType"); // TODO: implement method
    }

    @Override
    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getConcurrency"); // TODO: implement method
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#rowUpdated"); // TODO: implement method
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#rowInserted"); // TODO: implement method
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#rowDeleted"); // TODO: implement method
    }



    @Override
    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#insertRow"); // TODO: implement method
    }

    @Override
    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRow"); // TODO: implement method
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#deleteRow"); // TODO: implement method
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#refreshRow"); // TODO: implement method
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#cancelRowUpdates"); // TODO: implement method
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#moveToInsertRow"); // TODO: implement method
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#moveToCurrentRow"); // TODO: implement method
    }

    @Override
    public Statement getStatement() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getStatement"); // TODO: implement method
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRef"); // TODO: implement method
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBlob"); // TODO: implement method
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getClob"); // TODO: implement method
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getArray"); // TODO: implement method
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRef"); // TODO: implement method
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBlob"); // TODO: implement method
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getClob"); // TODO: implement method
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getArray"); // TODO: implement method
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getURL"); // TODO: implement method
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getURL"); // TODO: implement method
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRef"); // TODO: implement method
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRef"); // TODO: implement method
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateBlob"); // TODO: implement method
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateBlob"); // TODO: implement method
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateClob"); // TODO: implement method
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateClob"); // TODO: implement method
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateArray"); // TODO: implement method
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateArray"); // TODO: implement method
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRowId"); // TODO: implement method
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRowId"); // TODO: implement method
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRowId"); // TODO: implement method
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRowId"); // TODO: implement method
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getHoldability"); // TODO: implement method
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNString"); // TODO: implement method
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNString"); // TODO: implement method
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNClob"); // TODO: implement method
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNClob"); // TODO: implement method
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNClob"); // TODO: implement method
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNClob"); // TODO: implement method
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getSQLXML"); // TODO: implement method
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getSQLXML"); // TODO: implement method
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateSQLXML"); // TODO: implement method
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateSQLXML"); // TODO: implement method
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNString"); // TODO: implement method
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNString"); // TODO: implement method
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNCharacterStream"); // TODO: implement method
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNCharacterStream"); // TODO: implement method
    }



    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}