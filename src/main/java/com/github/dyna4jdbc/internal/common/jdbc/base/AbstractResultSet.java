package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.util.Iterator;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.jdbc.base.GuardedResultSetState.State;

public abstract class AbstractResultSet<T> extends AbstractReadOnlyResultSet {

    private final GuardedResultSetState resultSetState = new GuardedResultSetState();
	private final Statement statement;
	private SQLWarning sqlWarning;
    
    private final Iterator<T> rowIterator;
    private int row = 0;
	private int fetchSize;

    private T currentRow = null;

    public AbstractResultSet(Iterator<T> dataRowIterator, Statement statement) {
        this.rowIterator = dataRowIterator;
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
    
    protected T getCurrentRow() throws SQLException {
        if (currentRow == null) {
            throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException(
                    "currentRow is null in state: " + resultSetState);
        }
        
        return currentRow;
    }

    @Override
    protected void checkNotClosed() throws java.sql.SQLException {
        super.checkNotClosed("ResultSet");
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
     	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#isFirst()");
    }

    @Override
    public boolean isLast() throws SQLException {
    	return resultSetState.isInState(State.ITERATING_OVER_RESULTS) && !rowIterator.hasNext();
    }

    @Override
    public void beforeFirst() throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#beforeFirst()");
    }

    @Override
    public void afterLast() throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#afterLast()");
    }
    
    @Override
    public boolean first() throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#first()");
    }

    @Override
    public boolean last() throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#last()");
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
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"Moving cursor by absolute(int)");
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"Moving cursor by relative(int)");
    }

    @Override
    public boolean previous() throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"Moving cursor by previous()");
    }
    
    @Override
    public void setFetchDirection(int direction) throws SQLException {
    	if(direction != ResultSet.FETCH_FORWARD &&
    			direction != ResultSet.FETCH_REVERSE && 
    			direction != ResultSet.FETCH_UNKNOWN) {
    		SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException(
    				"Invalid direction:" + direction);
    	}
    	
    	if(direction != ResultSet.FETCH_FORWARD) {
    		SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
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
        	SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException(
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
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.RowId");
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.RowId");
    }
    
    @Override
    public Ref getRef(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Ref");
    }
    
    @Override
    public Ref getRef(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Ref");
    }
    
    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Blob");
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Clob");
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Array");
    }
    
    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Blob");
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Clob");
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.Blob");
    }
    
    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.NClob");
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.NClob");
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.SQLXML");
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.SQLXML");
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#getNString(int)");
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#getNString(String)");
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNCharacterStream"); // TODO: implement method
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
    	return getNCharacterStream(findColumn(columnLabel));
    }


    
}
