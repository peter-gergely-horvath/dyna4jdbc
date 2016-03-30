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

import com.github.dyna4jdbc.internal.SQLError;


public abstract class AbstractConnection extends BasicSQLObject implements java.sql.Connection{

    // TODO unify unsupported method handling in this class
    // --- properties used only to provide a sensible default JDBC interface implementation ---
    private boolean readOnly;
    private LinkedHashMap<String, Class<?>> typeMap = new LinkedHashMap<String, Class<?>>();
    private Properties clientInfo = new Properties();
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
    // ----------------------------------------------------------------------------------------

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public String nativeSQL(String sql) throws SQLException {
        checkNotClosed();
        return sql;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkNotClosed();
        if(! autoCommit) {
            throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
            		"This driver can only handle autocommit mode");
        }
    }

    public boolean getAutoCommit() throws SQLException {
        checkNotClosed();
        return true;
    }

    public void commit() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This driver can only handle autocommit mode");
    }

    public void rollback() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This driver can only handle autocommit mode");
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        checkNotClosed();
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() throws SQLException {
        checkNotClosed();
        return this.readOnly;
    }

    public void setCatalog(String catalog) throws SQLException {
        checkNotClosed();
        // "If the driver does not support catalogs, it will silently ignore this request."
    }

    public String getCatalog() throws SQLException {
        checkNotClosed();
        return null; // "the current catalog name or null if there is none"
    }

    public void setTransactionIsolation(int level) throws SQLException {
        checkNotClosed();
        if(level != Connection.TRANSACTION_NONE) {
            throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
            		"This driver does not support transaction isolation");
        }
    }

    public int getTransactionIsolation() throws SQLException {
        checkNotClosed();
        return Connection.TRANSACTION_NONE;
    }

    public SQLWarning getWarnings() throws SQLException {
        checkNotClosed();
        return null;
    }

    public void clearWarnings() throws SQLException {
        checkNotClosed();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        checkNotClosed();
        return Collections.unmodifiableMap(typeMap);
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        checkNotClosed();
        this.typeMap = new LinkedHashMap<String, Class<?>>(map);
    }

    public void setHoldability(int holdability) throws SQLException {
        checkNotClosed();

        if(holdability != ResultSet.HOLD_CURSORS_OVER_COMMIT &&
                holdability != ResultSet.CLOSE_CURSORS_AT_COMMIT) {
            throw new SQLException("Invalid holdability: " + holdability);
        }

        this.holdability = holdability;
    }

    public int getHoldability() throws SQLException {
        checkNotClosed();

        return holdability;
    }

    public Savepoint setSavepoint() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public Clob createClob() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public Blob createBlob() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public NClob createNClob() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public SQLXML createSQLXML() throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public boolean isValid(int timeout) throws SQLException {
        checkNotClosed();
        return true;
    }

    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        this.clientInfo.setProperty(name, value);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        this.clientInfo = new Properties(properties);
    }

    public String getClientInfo(String name) throws SQLException {
        checkNotClosed();
        return this.clientInfo.getProperty(name);
    }

    public Properties getClientInfo() throws SQLException {
        checkNotClosed();
        return new Properties(this.clientInfo);
    }

    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        checkNotClosed();
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
        		"This method is not supported");
    }

    public void setSchema(String schema) throws SQLException {
        checkNotClosed();
    }

    public String getSchema() throws SQLException {
        return null;
    }

    public void abort(Executor executor) throws SQLException {
        close();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        checkNotClosed();
        if(milliseconds < 0) throw new SQLException("milliseconds cannot be less than zero: " + milliseconds);
    }

    public int getNetworkTimeout() throws SQLException {
        checkNotClosed();
        return 0;
    }
}
