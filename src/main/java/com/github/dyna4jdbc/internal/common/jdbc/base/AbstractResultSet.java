package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.GuardedResultSetState.State;

public abstract class AbstractResultSet<T> extends AbstractReadOnlyResultSet {

    private final GuardedResultSetState resultSetState = new GuardedResultSetState();
	private final Statement statement;

    protected boolean wasNull = false;

    private SQLWarning sqlWarning;
    
    private final Iterator<T> rowIterator;
    private int row = 0;
	private int fetchSize;

    private T currentRow = null;

    public AbstractResultSet(Iterable<T> dataRowIterator, Statement statement) {
        this.rowIterator = dataRowIterator.iterator();
        this.statement = statement;
    }

    public boolean next() throws SQLException {
        checkNotClosed();

        GuardedResultSetState.State currentState = resultSetState.getCurrentState();
        switch (currentState) {
            case BEFORE_FIRST: {
                if(rowIterator.hasNext()) {
                    resultSetState.transitionTo(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
                    currentRow = rowIterator.next();
                    ++row;
                } else {
                    resultSetState.transitionTo(GuardedResultSetState.State.AFTER_LAST);
                }

                return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
            }



            case ITERATING_OVER_RESULTS: {
                if(rowIterator.hasNext()) {
                    currentRow = rowIterator.next();
                    ++row;
                } else {
                    resultSetState.transitionTo(GuardedResultSetState.State.AFTER_LAST);
                }

                return resultSetState.isInState(GuardedResultSetState.State.ITERATING_OVER_RESULTS);
            }

            case AFTER_LAST: {
                throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Calling next() in state " + currentState);
            }



            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException("Unexpected currentState: " + currentState);
        }
    }

    
    protected void skipNextRowIfPresent() {
    	if(this.rowIterator.hasNext()) {
    		rowIterator.next();
    	}
    }
    
    protected void checkValidStateForRowAccess() throws SQLException {
        checkNotClosed();
        resultSetState.checkValidStateForRowAccess();
    }
    
    protected T getCurrentRow() throws SQLException {
        if (currentRow == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                    "currentRow is null in state: " + resultSetState);
        }
        
        return currentRow;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return resultSetState.isInState(State.BEFORE_FIRST);
    }
    
    @Override
    public Statement getStatement() throws SQLException {
        return statement;
    }


    @Override
    public boolean isAfterLast() throws SQLException {
    	return resultSetState.isInState(State.AFTER_LAST);
    }

    @Override
    public boolean isFirst() throws SQLException {
     	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#isFirst()");
    }

    @Override
    public boolean isLast() throws SQLException {
    	return resultSetState.isInState(State.ITERATING_OVER_RESULTS) && !rowIterator.hasNext();
    }

    @Override
    public void beforeFirst() throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#beforeFirst()");
    }

    @Override
    public void afterLast() throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#afterLast()");
    }
    
    @Override
    public boolean first() throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#first()");
    }

    @Override
    public boolean last() throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#last()");
    }

    @Override
    public int getRow() throws SQLException {
    	return row;
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.sqlWarning;
    }

    @Override
    public void clearWarnings() throws SQLException {
    	this.sqlWarning = null;
    }
    
    protected void setWarnings(SQLWarning sqlWarning) {
    	this.sqlWarning = sqlWarning;
    }
    
    @Override
    public boolean absolute(int row) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"Moving cursor by absolute(int)");
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"Moving cursor by relative(int)");
    }

    @Override
    public boolean previous() throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"Moving cursor by previous()");
    }
    
    @Override
    public void setFetchDirection(int direction) throws SQLException {
    	if(direction != ResultSet.FETCH_FORWARD &&
    			direction != ResultSet.FETCH_REVERSE && 
    			direction != ResultSet.FETCH_UNKNOWN) {
    		JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
    				"Invalid direction:" + direction);
    	}
    	
    	if(direction != ResultSet.FETCH_FORWARD) {
    		JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
    				"Only FETCH_FORWARD fetch direction is supported: " + direction);
    	}
    }

    @Override
    public int getFetchDirection() throws SQLException {
    	return ResultSet.FETCH_FORWARD;
    }
    
    @Override
    public void setFetchSize(int rows) throws SQLException {
        if(rows < 0) {
        	JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
    				"Negative fetch size: " + rows);
        }
        this.fetchSize = rows;
    }
    

    @Override
    public int getFetchSize() throws SQLException {
        return this.fetchSize;
    }
    
    @Override
    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public int getConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }
    
    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.RowId");
    }

    @Override
    public final RowId getRowId(String columnLabel) throws SQLException {
    	return getRowId(findColumn(columnLabel));
    }
    
    @Override
    public Ref getRef(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.Ref");
    }
    
    @Override
    public final Ref getRef(String columnLabel) throws SQLException {
    	return getRef(findColumn(columnLabel));
    }
    
    @Override
    public final Blob getBlob(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.Blob");
    }

    @Override
    public final Clob getClob(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.Clob");
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.Array");
    }
    
    @Override
    public final Blob getBlob(String columnLabel) throws SQLException {
    	return getBlob(findColumn(columnLabel));
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
    	return getClob(findColumn(columnLabel));
    }

    @Override
    public final Array getArray(String columnLabel) throws SQLException {
    	return getArray(findColumn(columnLabel));
    }
    
    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.NClob");
    }

    @Override
    public final NClob getNClob(String columnLabel) throws SQLException {
    	return getNClob(findColumn(columnLabel));
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.SQLXML");
    }

    @Override
    public final SQLXML getSQLXML(String columnLabel) throws SQLException {
    	return getSQLXML(findColumn(columnLabel));
    }

    @Override
    public final String getNString(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#getNString(int)");
    }

    @Override
    public final String getNString(String columnLabel) throws SQLException {
    	return getNString(findColumn(columnLabel));
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
    	throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("java.sql.ResultSet#getNCharacterStream(int)");
    }

    @Override
    public final Reader getNCharacterStream(String columnLabel) throws SQLException {
    	return getNCharacterStream(findColumn(columnLabel));
    }


    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return getCharacterStream(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(findColumn(columnLabel), map);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return getDate(findColumn(columnLabel), cal);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return getTime(findColumn(columnLabel), cal);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnLabel), cal);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return getURL(findColumn(columnLabel));
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    protected  <T> T setWasNullBasedOnLastValue(T convertedValue) {
        wasNull = (convertedValue == null);
        return convertedValue;
    }

    @Override
    public boolean wasNull() throws SQLException {
        return wasNull;
    }

    @Override
    public abstract int findColumn(String columnLabel) throws SQLException;

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return getObject(findColumn(columnLabel), type);
    }
}
