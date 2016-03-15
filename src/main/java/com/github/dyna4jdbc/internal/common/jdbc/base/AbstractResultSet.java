package com.github.dyna4jdbc.internal.common.jdbc.base;

import com.github.dyna4jdbc.internal.ClosableSQLObject;
import com.github.dyna4jdbc.internal.SQLError;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;

public abstract class AbstractResultSet<T> extends ClosableSQLObject implements java.sql.ResultSet {

    protected final GuardedResultSetState resultSetState = new GuardedResultSetState();

    private final Iterator<T> rowIterator;

    protected T currentRow = null;

    public AbstractResultSet(Iterator<T> dataRowIterator) {
        this.rowIterator = dataRowIterator;
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

    protected void checkValidStateForRowAccess() throws SQLException {
        checkNotClosed();
        resultSetState.checkValidStateForRowAccess();
    }

    @Override
    protected void checkNotClosed() throws java.sql.SQLException {
        super.checkNotClosed("ResultSet");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("Updating ResultSet data");
    }
}
