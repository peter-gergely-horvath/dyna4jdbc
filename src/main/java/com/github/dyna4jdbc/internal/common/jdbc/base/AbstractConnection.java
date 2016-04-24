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


public abstract class AbstractConnection extends BasicSQLObject implements java.sql.Connection{

    // --- properties used only to provide a sensible default JDBC interface implementation ---
    private boolean readOnly;
    private LinkedHashMap<String, Class<?>> typeMap = new LinkedHashMap<String, Class<?>>();
    private Properties clientInfo = new Properties();
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
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
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        checkNotClosed();
        return sql;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkNotClosed();
        if(! autoCommit) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
            		"This driver can only handle autocommit mode");
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        checkNotClosed();
        return true;
    }

    @Override
    public void commit() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This driver can only handle autocommit mode");
    }

    @Override
    public void rollback() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This driver can only handle autocommit mode");
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkNotClosed();
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        checkNotClosed();
        return this.readOnly;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        checkNotClosed();
        // "If the driver does not support catalogs, it will silently ignore this request."
    }

    @Override
    public String getCatalog() throws SQLException {
        checkNotClosed();
        return null; // "the current catalog name or null if there is none"
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        checkNotClosed();
        if(level != Connection.TRANSACTION_NONE) {
            throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
            		"This driver does not support transaction isolation");
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        checkNotClosed();
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkNotClosed();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkNotClosed();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkNotClosed();
        return Collections.unmodifiableMap(typeMap);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();
        this.typeMap = new LinkedHashMap<String, Class<?>>(map);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        checkNotClosed();

        if(holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT &&
                holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {
            throw new SQLException("Invalid holdability: " + holdability);
        }

        this.holdability = holdability;
    }

    @Override
    public int getHoldability() throws SQLException {
        checkNotClosed();

        return holdability;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public Clob createClob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public Blob createBlob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public NClob createNClob() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        checkNotClosed();
        return true;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.clientInfo.setProperty(name, value);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.clientInfo = new Properties(properties);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        checkNotClosed();
        return this.clientInfo.getProperty(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        checkNotClosed();
        return new Properties(this.clientInfo);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        checkNotClosed();
        throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException(
        		"This method is not supported");
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        checkNotClosed();
    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkNotClosed();
        if(milliseconds < 0) throw new SQLException("milliseconds cannot be less than zero: " + milliseconds);
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        checkNotClosed();
        return 0;
    }

    @Override
	public void abort(Executor executor) throws SQLException {

		try {
			executor.execute(new CloseConnectionForAbortRunnable());
		} catch (RejectedExecutionException ree) {
			JDBCError.CLOSE_FAILED.raiseSQLException(ree, 
					"The close task has been rejected");
		}

	}
    
    private final class CloseConnectionForAbortRunnable implements Runnable {
		@Override
		public void run() {
			try {
				close();
			} catch (SQLException ex) {
				JDBCError.CLOSE_FAILED.raiseUncheckedException(ex, 
						"Async close failed, examine chained exception stack trace for root cause");
			}
		}
	}
}
