package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.ClosableSQLObject;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.impl.DummyScriptOutputHandlerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.Writer;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executor;

public class ScriptEngineConnection extends ClosableSQLObject implements java.sql.Connection {

    final String engineName;

    private final String configuration;
    private final Properties properties;

    private final ScriptEngine engine;

    // --- properties used only to provide a sensible default JDBC interface implementation ---
    private boolean readOnly;
    private LinkedHashMap<String, Class<?>> typeMap = new LinkedHashMap<String, Class<?>>();
    private Properties clientInfo = new Properties();
    private int holdability = ResultSet.CLOSE_CURSORS_AT_COMMIT;
    // ----------------------------------------------------------------------------------------

    public ScriptEngineConnection(String parameters, Properties properties)
    {
        this.properties = properties;

        if(parameters == null || "".equals(parameters.trim())) {
            throw new IllegalArgumentException("Scrip Engine Name not specified");
        }

        String[] engineNameAndParameters = parameters.split(":", 2);

        engineName = engineNameAndParameters[0];
        configuration = engineNameAndParameters.length == 2 ? engineNameAndParameters[1] : null;

        if(engineName == null || "".equals(engineName.trim())) {
            throw new IllegalArgumentException("Scrip Engine Name not specified");
        }

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.engine = scriptEngineManager.getEngineByName(engineName);
        if(this.engine == null) {
            throw new IllegalArgumentException("ScriptEngine not found: " + engineName);
        }

    }

    public Statement createStatement() throws SQLException {
        checkNotClosed();
        return new ScriptEngineStatement(this, new DummyScriptOutputHandlerFactory(this));
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public String nativeSQL(String sql) throws SQLException {
        checkNotClosed();
        return sql;
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkNotClosed();
        if(! autoCommit) {
            throw new SQLException("This driver can only handle autocommit mode");
        }
    }

    public boolean getAutoCommit() throws SQLException {
        checkNotClosed();
        return true;
    }

    public void commit() throws SQLException {
        checkNotClosed();
        throw new SQLException("This driver can only handle autocommit mode");
    }

    public void rollback() throws SQLException {
        checkNotClosed();
        throw new SQLException("This driver can only handle autocommit mode");
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        checkNotClosed();
        return new ScriptEngineDatabaseMetaData(this);
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
            throw new SQLException("This driver does not support transaction isolation");
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
        throw new SQLException("This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
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
        throw new SQLException("This method is not supported");
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public Clob createClob() throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public Blob createBlob() throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public NClob createNClob() throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
    }

    public SQLXML createSQLXML() throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
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
        throw new SQLException("This method is not supported");
    }

    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        checkNotClosed();
        throw new SQLException("This method is not supported");
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

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public void close() throws SQLException {
        super.close();
    }

    interface ScriptEngineCallback<T> {
        T execute(ScriptEngine engine) throws ScriptException;
    }


    void executeUsingScriptEngine(ScriptEngineCallback<Void> scriptEngineCallback) throws ScriptException {

        synchronized (engine) {
            Writer originalWriter = engine.getContext().getWriter();
            Writer originalErrorWriter = engine.getContext().getErrorWriter();

            try {

                scriptEngineCallback.execute(engine);
            }
            finally {
                engine.getContext().setWriter(originalWriter);
                engine.getContext().setErrorWriter(originalErrorWriter);
            }
        }




    }
}
