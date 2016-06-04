package com.github.dyna4jdbc.internal.common.jdbc.base;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.generic.EmptyResultSet;
import com.github.dyna4jdbc.internal.common.util.sqlwarning.SQLWarningUtils;

public abstract class AbstractStatement<T extends java.sql.Connection>
        extends AbstractAutoCloseableJdbcObject implements java.sql.Statement {

    private static final int INVALID_UPDATE_COUNT = -1;


    private final T connection;


    private SQLWarning sqlWarning;
    private Iterator<ResultSet> resultSetIterator;
    private int currentUpdateCount;

    public AbstractStatement(T connection) {
        this.connection = connection;
    }

    @Override
    public final T getConnection() throws SQLException {
        return connection;
    }

    protected final void setUpdateCount(int updateCount) {
        this.currentUpdateCount = updateCount;
    }

    protected final void clearUpdateCount() {
        this.currentUpdateCount = INVALID_UPDATE_COUNT;
    }

    @Override
    public final int getUpdateCount() throws SQLException {
        return this.currentUpdateCount;
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        return sqlWarning;
    }

    protected final void addSQLWarning(SQLWarning warning) {

        this.sqlWarning = SQLWarningUtils.chainSQLWarning(this.sqlWarning, warning);
    }


    @Override
    public final void clearWarnings() throws SQLException {
        sqlWarning = null;
    }

    @Override
    public final boolean getMoreResults(int current) throws SQLException {
        if (current != Statement.CLOSE_CURRENT_RESULT
                && current != Statement.KEEP_CURRENT_RESULT
                && current != Statement.CLOSE_ALL_RESULTS) {

            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid value for current: " + current);
        }

        if (current != Statement.CLOSE_CURRENT_RESULT) {

            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "Only Statement.CLOSE_CURRENT_RESULT is supported: " + current);
        }

        return getMoreResults();
    }

    @Override
    public final void setFetchDirection(int direction) throws SQLException {
        if (direction != ResultSet.FETCH_FORWARD
                && direction != ResultSet.FETCH_REVERSE
                && direction != ResultSet.FETCH_UNKNOWN) {

            // signal illegal argument
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid direction: " + direction);
        }

        // no-op: setFetchDirection is just a hint, a driver might ignore it
    }

    @Override
    public final int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public final void setFetchSize(int rows) throws SQLException {
        if (rows < 0) {
            throw new SQLException("Invalid fetchSize: " + rows);
        }

        if (rows > 0) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "Setting a non-zero fetchSize: " + rows);
        }
    }

    @Override
    public final ResultSet getResultSet() throws SQLException {

        ResultSet resultSetToReturn;
        if (resultSetIterator != null && resultSetIterator.hasNext()) {
            resultSetToReturn = resultSetIterator.next();
        } else {
            resultSetToReturn = null;
        }

        return resultSetToReturn;
    }

    protected final void setCurrentResultSetList(List<ResultSet> currentResultSetList) {
        resultSetIterator = currentResultSetList.iterator();
    }

    @Override
    public final boolean getMoreResults() throws SQLException {
        return resultSetIterator != null && resultSetIterator.hasNext();
    }


    @Override
    public final int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public final int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public final int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        return new EmptyResultSet();
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public final void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public final int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public final void setMaxFieldSize(int max) throws SQLException {
        // TODO: implement
    }

    @Override
    public final int getMaxRows() throws SQLException {
        return 0; // no limit
    }

    @Override
    public final void setMaxRows(int max) throws SQLException {
        // TODO: implement
    }

    @Override
    public final void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public final int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public final void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {
        // No-op: "If the database does not support positioned update/delete, this method is a noop"
    }

    @Override
    public final boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public final void closeOnCompletion() throws SQLException {
        // TODO: implement
    }

    @Override
    public final boolean isCloseOnCompletion() throws SQLException {
        return false; // TODO: implement
    }

    // -- unsupported JDBC operations
    public final void addBatch(String sql) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.addBatch(String)");
    }

    public final void clearBatch() throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.clearBatch()");
    }

    public final int[] executeBatch() throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeBatch()");
    }

    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, int)");
    }

    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, int[])");
    }

    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, String[])");
    }

    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, int)");
    }

    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, int[])");
    }

    public final boolean execute(String sql, String[] columnNames) throws SQLException {
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, String[])");
    }
}
