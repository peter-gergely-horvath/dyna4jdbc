package com.github.dyna4jdbc.internal.common.jdbc.base;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.sqlwarning.SQLWarningUtils;


public abstract class AbstractConnection extends AbstractAutoCloseableJdbcObject implements java.sql.Connection {

    
    private static final int SUPPORTED_HOLDABILITY = ResultSet.HOLD_CURSORS_OVER_COMMIT;

    // --- properties used only to provide a sensible default JDBC interface implementation ---
    private LinkedHashMap<String, Class<?>> typeMap = new LinkedHashMap<String, Class<?>>();
    private Properties clientInfo = new Properties();

    private SQLWarning sqlWarning;
    // ----------------------------------------------------------------------------------------

    @Override
    public final Statement createStatement() throws SQLException {
        checkNotClosed();

        AbstractStatement<?> createdStatement = createStatementInternal();

        registerAsChild(createdStatement);

        return createdStatement;
    }

    protected abstract AbstractStatement<?> createStatementInternal() throws SQLException;
    
    @Override
    public final Statement createStatement(
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkNotClosed();

        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
            && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY) {

            return createStatement();
        }

        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "Creating non-forward-only or non read-only statements");
    }
    
    @Override
    public final Statement createStatement(
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkNotClosed();
        
        if (resultSetType == ResultSet.TYPE_FORWARD_ONLY
                && resultSetConcurrency == ResultSet.CONCUR_READ_ONLY
                && resultSetHoldability == SUPPORTED_HOLDABILITY) {

                return createStatement();
            }

        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "Creating non-forward-only, non read-only or not over-commit held statements");
    }

    @Override
    public final PreparedStatement prepareStatement(String sql) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final CallableStatement prepareCall(String sql) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final String nativeSQL(String sql) throws SQLException {
        checkNotClosed();
        return sql;
    }

    @Override
    public final void setAutoCommit(boolean autoCommit) throws SQLException {
        checkNotClosed();
        if (!autoCommit) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "This driver can only handle autocommit mode");
        }
    }

    @Override
    public final boolean getAutoCommit() throws SQLException {
        checkNotClosed();
        return true;
    }

    @Override
    public final void commit() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This driver can only handle autocommit mode");
    }

    @Override
    public final void rollback() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This driver can only handle autocommit mode");
    }

    @Override
    public final Savepoint setSavepoint() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Savepoint setSavepoint(String name) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void rollback(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void setReadOnly(boolean readOnly) throws SQLException {
        checkNotClosed();
        if (!readOnly) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "This driver can only handle read-only mode");
        }
    }

    @Override
    public final boolean isReadOnly() throws SQLException {
        checkNotClosed();
        return true;
    }

    @Override
    public final void setCatalog(String catalog) throws SQLException {
        checkNotClosed();
        // "If the driver does not support catalogs, it will silently ignore this request."
    }

    @Override
    public final String getCatalog() throws SQLException {
        checkNotClosed();
        return null; // "the current catalog name or null if there is none"
    }

    @Override
    public final void setTransactionIsolation(int level) throws SQLException {
        checkNotClosed();
        if (level != Connection.TRANSACTION_NONE) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "This driver does not support transaction isolation");
        }
    }

    @Override
    public final int getTransactionIsolation() throws SQLException {
        checkNotClosed();
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        checkNotClosed();
        return this.sqlWarning;
    }

    @Override
    public final void clearWarnings() throws SQLException {
        checkNotClosed();
        this.sqlWarning = null;
    }

    protected final void addSQLWarning(SQLWarning warning) {

        this.sqlWarning = SQLWarningUtils.chainSQLWarning(this.sqlWarning, warning);
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final CallableStatement prepareCall(
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Map<String, Class<?>> getTypeMap() throws SQLException {
        checkNotClosed();
        return Collections.unmodifiableMap(typeMap);
    }

    @Override
    public final void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();
        this.typeMap = new LinkedHashMap<String, Class<?>>(map);
    }

    @Override
    public final void setHoldability(int holdability) throws SQLException {
        checkNotClosed();

        if (holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT
                && holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {

            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid holdability: " + holdability);
        }

        if (holdability != SUPPORTED_HOLDABILITY) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                    "Unsupported holdability: " + holdability);
        }
    }

    @Override
    public final int getHoldability() throws SQLException {
        checkNotClosed();

        return SUPPORTED_HOLDABILITY;
    }



    @Override
    public final PreparedStatement prepareStatement(
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final CallableStatement prepareCall(
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql, int autoGeneratedKeys) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql, int[] columnIndexes) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final PreparedStatement prepareStatement(
            String sql, String[] columnNames) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Clob createClob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Blob createBlob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final NClob createNClob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final SQLXML createSQLXML() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final boolean isValid(int timeout) throws SQLException {

        if (timeout < 0) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Negative timeout: " + timeout);
        }

        return !isClosed();
    }

    @Override
    public final void setClientInfo(
            String name, String value) throws SQLClientInfoException {

        this.clientInfo.setProperty(name, value);
    }

    @Override
    public final void setClientInfo(
            Properties properties) throws SQLClientInfoException {

        // Caution: Properties constructor sets the
        // *defaults* of the Properties
        this.clientInfo = new Properties();
        this.clientInfo.putAll(properties);
    }

    @Override
    public final String getClientInfo(String name) throws SQLException {
        checkNotClosed();
        return this.clientInfo.getProperty(name);
    }

    @Override
    public final Properties getClientInfo() throws SQLException {
        checkNotClosed();
        return new Properties(this.clientInfo);
    }

    @Override
    public final Array createArrayOf(
            String typeName, Object[] elements) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final Struct createStruct(
            String typeName, Object[] attributes) throws SQLException {

        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
                "This method is not supported");
    }

    @Override
    public final void setSchema(String schema) throws SQLException {
        checkNotClosed();
        // No-op: "If the driver does not support schemas, it will silently ignore this request."
    }

    @Override
    public final String getSchema() throws SQLException {
        checkNotClosed();
        // "the current schema name or null if there is none"
         return null;
    }

    @Override
    public final void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkNotClosed();
        if (milliseconds < 0) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "milliseconds cannot be less than zero: " + milliseconds);
        }
    }

    @Override
    public final int getNetworkTimeout() throws SQLException {
        checkNotClosed();
        return 0;
    }

    @Override
    public final void abort(Executor executor) throws SQLException {
        if (!isClosed()) {
            try {
                executor.execute(new CloseConnectionForAbortRunnable());
            } catch (RejectedExecutionException ree) {
                // Best effort handling of unexpected failures:
                // still mark this as closed.
                markClosedInternal();
                JDBCError.CLOSE_FAILED.raiseSQLException(ree,
                        this, "The close task has been rejected");
            }
        }
    }

    private final class CloseConnectionForAbortRunnable implements Runnable {
        @Override
        public void run() {
            try {
                close();
            } catch (SQLException ex) {
                JDBCError.CLOSE_FAILED.raiseUncheckedException(ex,
                        AbstractConnection.this, "Async close failed, "
                                + "examine chained exception stack trace "
                                + "for root cause");
            }
        }
    }
}
