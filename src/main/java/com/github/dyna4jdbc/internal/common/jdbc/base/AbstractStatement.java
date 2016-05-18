package com.github.dyna4jdbc.internal.common.jdbc.base;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.generic.EmptyResultSet;

public abstract class AbstractStatement<T extends java.sql.Connection> extends AbstractAutoCloseableJdbcObject implements java.sql.Statement {

    protected final T connection;


    private SQLWarning sqlWarning;
    private Iterator<ResultSet> resultSetIterator;
    private int currentUpdateCount;

    public AbstractStatement(T connection) {
        this.connection = connection;
    }

    @Override
    public T getConnection() throws SQLException {
        return connection;
    }

    protected void setUpdateCount(int updateCount) {
        this.currentUpdateCount = updateCount;
    }

    protected void clearUpdateCount() {
        this.currentUpdateCount = -1;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return this.currentUpdateCount;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return sqlWarning;
    }

    protected void setSQLWarning(SQLWarning warning) {
        this.sqlWarning = warning;
    }

    @Override
    public void clearWarnings() throws SQLException {
        sqlWarning = null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        if(current != Statement.CLOSE_CURRENT_RESULT &&
                current != Statement.KEEP_CURRENT_RESULT &&
                current != Statement.CLOSE_ALL_RESULTS) {
            throw new SQLException("Invalid value for current: " + current);
        }

        if(current != Statement.CLOSE_CURRENT_RESULT) {
            throw new SQLException(
                    "Only Statement.CLOSE_CURRENT_RESULT is supported: " +
                            current);
        }
        return getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if(direction ==  ResultSet.FETCH_FORWARD ||
                direction == ResultSet.FETCH_REVERSE ||
                direction == ResultSet.FETCH_UNKNOWN) {
            // no-op: setFetchDirection is just a hint, a driver might ignore it
        }
        else {
            // signal illegal argument
            throw new SQLException("Invalid direction: " + direction);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        if(rows < 0) {
            throw new SQLException("Invalid fetchSize: " + rows);
        }

        if(rows > 0) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "Setting a non-zero fetchSize: " + rows);
        }
    }

    @Override
    public ResultSet getResultSet() throws SQLException {

        ResultSet resultSetToReturn;
        if(resultSetIterator != null && resultSetIterator.hasNext()) {
            resultSetToReturn = resultSetIterator.next();
        } else {
            resultSetToReturn = null;
        }

        return resultSetToReturn;
    }

    protected void setCurrentResultSetList(List<ResultSet> currentResultSetList) {
        resultSetIterator = currentResultSetList.iterator();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return resultSetIterator != null && resultSetIterator.hasNext();
    }


    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return new EmptyResultSet();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        // TODO: implement
    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0; // no limit
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        // TODO: implement
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {
        JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.cancel()");
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        // No-op: "If the database does not support positioned update/delete, this method is a noop"
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        // TODO: implement
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false; // TODO: implement
    }

    // -- unsupported JDBC operations
    public void addBatch(String sql) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.addBatch(String)");
    }

    public void clearBatch() throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.clearBatch()");
    }

    public int[] executeBatch() throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeBatch()");
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, int)");
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, int[])");
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, String[])");
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, int)");
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, int[])");
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, String[])");
    }
}
