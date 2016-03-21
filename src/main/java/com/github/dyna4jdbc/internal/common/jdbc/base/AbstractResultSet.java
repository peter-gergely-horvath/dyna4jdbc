package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.jdbc.base.GuardedResultSetState.State;

public abstract class AbstractResultSet<T> extends AbstractReadOnlyResultSet {

    private final GuardedResultSetState resultSetState = new GuardedResultSetState();
	private final Statement statement;
    
    
    private final Iterator<T> rowIterator;

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
    	throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException("java.sql.ResultSet#getRow()");
    }
}
