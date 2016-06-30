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

    public static final int INVALID_UPDATE_COUNT = -1;
    public static final int ZERO_UPDATE_COUNT = 0;


    private final T connection;


    private SQLWarning sqlWarning;
    private Iterator<ResultSet> resultSetIterator;
    private int currentUpdateCount = INVALID_UPDATE_COUNT;

    private int maxRows = 0;

    public AbstractStatement(T connection) {
        super(connection);
        this.connection = connection;
    }

    @Override
    public final T getConnection() throws SQLException {
        checkNotClosed();

        return connection;
    }

    public final void setUpdateCount(int updateCount) {
        this.currentUpdateCount = updateCount;
    }

    @Override
    public final int getUpdateCount() throws SQLException {
        checkNotClosed();

        return this.currentUpdateCount;
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        checkNotClosed();

        return sqlWarning;
    }

    protected final void addSQLWarning(SQLWarning warning) {

        this.sqlWarning = SQLWarningUtils.chainSQLWarning(this.sqlWarning, warning);
    }


    @Override
    public final void clearWarnings() throws SQLException {
        checkNotClosed();

        sqlWarning = null;
    }

    @Override
    public final boolean getMoreResults(int current) throws SQLException {
        checkNotClosed();

        if (current != Statement.CLOSE_CURRENT_RESULT
                && current != Statement.KEEP_CURRENT_RESULT
                && current != Statement.CLOSE_ALL_RESULTS) {

            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid value for current: " + current);
        }

        if (current != Statement.CLOSE_CURRENT_RESULT) {

            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "Only Statement.CLOSE_CURRENT_RESULT is supported: " + current);
        }

        return getMoreResults();
    }

    @Override
    public final void setFetchDirection(int direction) throws SQLException {
        checkNotClosed();

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
        checkNotClosed();

        return ResultSet.FETCH_FORWARD;
    }

    @Override
    public final void setFetchSize(int rows) throws SQLException {
        checkNotClosed();

        if (rows < 0) {
            // signal illegal argument: we expect non-negative value
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "fetchSize cannot be set to negative value: " + rows);
        }

        if (rows > 0) {
            throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                    "Setting a non-zero fetchSize: " + rows);
        }
    }

    @Override
    public final ResultSet getResultSet() throws SQLException {
        checkNotClosed();

        ResultSet resultSetToReturn;
        if (resultSetIterator != null && resultSetIterator.hasNext()) {
            resultSetToReturn = resultSetIterator.next();
        } else {
            resultSetToReturn = null;
        }

        return resultSetToReturn;
    }

    public final void setCurrentResultSetList(List<ResultSet> currentResultSetList) {
        resultSetIterator = currentResultSetList.iterator();
    }

    @Override
    public final boolean getMoreResults() throws SQLException {
        checkNotClosed();

        return resultSetIterator != null && resultSetIterator.hasNext();
    }


    @Override
    public final int getFetchSize() throws SQLException {
        checkNotClosed();

        return 0;
    }

    @Override
    public final int getResultSetConcurrency() throws SQLException {
        checkNotClosed();

        return ResultSet.CONCUR_READ_ONLY;
    }

    @Override
    public final int getResultSetType() throws SQLException {
        checkNotClosed();

        return ResultSet.TYPE_FORWARD_ONLY;
    }

    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        checkNotClosed();

        return new EmptyResultSet();
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {
        checkNotClosed();

        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public final void setPoolable(boolean poolable) throws SQLException {
        checkNotClosed();

        /*
        Implementation is no-op: this is just a HINT!

        From the JavaDoc: "the value specified is a hint to the statement pool
        implementation indicating whether the application wants the statement
        to be pooled. It is up to the statement pool manager as to whether
        the hint is used."
        */
    }

    @Override
    public final int getMaxFieldSize() throws SQLException {
        checkNotClosed();
        // "zero means there is no limit"
        return 0;
    }

    @Override
    public final void setMaxFieldSize(int max) throws SQLException {
        checkNotClosed();

        if(max == 0) {
            // call was made with zero (no limit), the only value we support currently
            // accept this setting by not doing anything
            return;
        }

        if(max < 0) {
            // signal illegal argument: we expect non-negative value
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "maxFieldSize cannot be set to negative value: " + max);
        }

        // max is a positive value: this is not implemented yet.
        // We clearly signal this to the caller by throwing an exception
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.setMaxFieldSize(int)");
    }

    @Override
    public final int getMaxRows() throws SQLException {
        checkNotClosed();
        return this.maxRows;
    }

    @Override
    public final void setMaxRows(int max) throws SQLException {
        checkNotClosed();

        if(max < 0) {
            // signal illegal argument: we expect non-negative value
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "maxRows cannot be set to negative value: " + max);
        }

        this.maxRows = max;
    }

    @Override
    public final void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public final int getQueryTimeout() throws SQLException {
        checkNotClosed();
        // "the current query timeout limit in seconds; zero means there is no limit"
        return 0;
    }

    @Override
    public final void setQueryTimeout(int seconds) throws SQLException {
        checkNotClosed();

        if(seconds == 0) {
            // call was made with zero (no limit), the only value we support currently
            // accept this setting by not doing anything
            return;
        }

        if(seconds < 0) {
            // signal illegal argument: we expect non-negative value
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "seconds cannot be set to negative value: " + seconds);
        }

        // max is a positive value: this is not implemented yet.
        // We clearly signal this to the caller by throwing an exception
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.setQueryTimeout(int)");
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        checkNotClosed();

        // No-op: "If the database does not support positioned update/delete, this method is a noop"
    }

    @Override
    public final boolean isPoolable() throws SQLException {
        checkNotClosed();

        return false;
    }

    @Override
    public final boolean isCloseOnCompletion() throws SQLException {
        checkNotClosed();

        return false;
    }

    // -- unsupported JDBC operations
    @Override
    public final void closeOnCompletion() throws SQLException {
        // Not implemented for now: we clearly signal this to the caller
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.closeOnCompletion()");
    }

    public final void addBatch(String sql) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.addBatch(String)");
    }

    public final void clearBatch() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.clearBatch()");
    }

    public final int[] executeBatch() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeBatch()");
    }

    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, int)");
    }

    public final int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, int[])");
    }

    public final int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.executeUpdate(String, String[])");
    }

    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, int)");
    }

    public final boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, int[])");
    }

    public final boolean execute(String sql, String[] columnNames) throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException(
                "java.sql.Statement.execute(String, String[])");
    }
}
