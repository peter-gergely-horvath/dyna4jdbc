package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.ClosableSQLObject;
import com.github.dyna4jdbc.internal.SQLError;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Writer;
import java.sql.*;
import java.util.Iterator;
import java.util.List;

abstract class AbstractScriptEngineStatement extends ClosableSQLObject implements java.sql.Statement {

    private final ScriptEngineConnection scriptEngineConnection;
    private SQLWarning sqlWarning;
    private Iterator<ResultSet> resultSetIterator;
    private int currentUpdateCount;


    AbstractScriptEngineStatement(ScriptEngineConnection scriptEngineConnection) {
        this.scriptEngineConnection = scriptEngineConnection;
    }


    public final ResultSet executeQuery(String script) throws SQLException {
        try {
            return executeScriptForSingleResultSet(script);
        } catch (ScriptException se) {
            throw SQLError.SCRIPT_EXECUTION_EXCEPTION.raiseException(se);
        } catch (Throwable t) {
            throw SQLError.UNEXPECTED_THROWABLE.raiseThrowable(t);
        }
    }

    protected abstract ResultSet executeScriptForSingleResultSet(String script) throws ScriptException;

    public final int executeUpdate(final String script) throws SQLException {

        try {
            return executeScriptForUpdateCount(script);

        } catch (ScriptException se) {
            throw SQLError.SCRIPT_EXECUTION_EXCEPTION.raiseException(se);
        } catch (Throwable t) {
            throw SQLError.UNEXPECTED_THROWABLE.raiseThrowable(t);
        }
    }

    protected abstract int executeScriptForUpdateCount(String script) throws ScriptException;

    public final boolean execute(final String script) throws SQLException {

        try {
            return executeScript(script, new ScriptResultHandler());
        } catch (ScriptException se) {
            throw SQLError.SCRIPT_EXECUTION_EXCEPTION.raiseException(se);
        } catch (Throwable t) {
            throw SQLError.UNEXPECTED_THROWABLE.raiseThrowable(t);
        }
    }


    protected final class ScriptResultHandler {

        protected final void onResultSets(List<ResultSet> resultSets) {
            setCurrentResultSetList(resultSets);
        }

        protected final void onUpdateCount(int updateCount) {
            setUpdateCount(updateCount);
        }
    }

    protected abstract boolean executeScript(
            String script, ScriptResultHandler scriptResultHandler) throws ScriptException;



    protected void setSQLWarning(SQLWarning warning) {
        this.sqlWarning = warning;
    }

    private void setCurrentResultSetList(List<ResultSet> currentResultSetList) {
        resultSetIterator = currentResultSetList.iterator();
    }

    protected void setUpdateCount(int updateCount) {
        this.currentUpdateCount = updateCount;
    }

    protected void clearUpdateCount() {
        this.currentUpdateCount = -1;
    }

    public ResultSet getResultSet() throws SQLException {

        ResultSet resultSetToReturn;
        if(resultSetIterator != null && resultSetIterator.hasNext()) {
            resultSetToReturn = resultSetIterator.next();
        } else {
            resultSetToReturn = null;
        }

        return resultSetToReturn;
    }

    public boolean getMoreResults() throws SQLException {
        return resultSetIterator.hasNext();
    }

    protected void executeScriptUsingCustomWriters(final String script, Writer outWriter, Writer errorWriter)
            throws ScriptException {

        ExecuteScriptUsingCustomPrintWriter customWritersCallback =
                new ExecuteScriptUsingCustomPrintWriter(script, outWriter, errorWriter);

        scriptEngineConnection.executeUsingScriptEngine(customWritersCallback);
    }

    private static final class ExecuteScriptUsingCustomPrintWriter
            implements ScriptEngineConnection.ScriptEngineCallback<Void> {

        private final String script;
        private final Writer outWriter;
        private final Writer errorWriter;

        private ExecuteScriptUsingCustomPrintWriter(String script, Writer outWriter, Writer errorWriter) {
            this.script = script;
            this.outWriter = outWriter;
            this.errorWriter = errorWriter;
        }

        public Void execute(ScriptEngine engine) throws ScriptException {

            if (outWriter != null) {
                engine.getContext().setWriter(outWriter);
            }

            if (errorWriter != null) {
                engine.getContext().setErrorWriter(errorWriter);
            }

            engine.eval(script);

            return null;
        }
    }



    // --- JDBC Statement Implementation
    public Connection getConnection() throws SQLException {
        return scriptEngineConnection;
    }

    public int getUpdateCount() throws SQLException {
        return this.currentUpdateCount;
    }

    public SQLWarning getWarnings() throws SQLException {
        return sqlWarning;
    }

    public void clearWarnings() throws SQLException {
        sqlWarning = null;
    }

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

    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    public void setFetchSize(int rows) throws SQLException {
        if(rows < 0) {
            throw new SQLException("Invalid fetchSize: " + rows);
        }

        if(rows > 0) {
            throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                    "Setting a non-zero fetchSize: " + rows);
        }
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public int getResultSetConcurrency() throws SQLException {
        return ResultSet.CONCUR_READ_ONLY;
    }

    public int getResultSetType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return new EmptyResultSet();
    }

    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    public void setPoolable(boolean poolable) throws SQLException {

    }

    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    public void setMaxFieldSize(int max) throws SQLException {
        // TODO: implement
    }

    public int getMaxRows() throws SQLException {
        return 0; // no limit
    }

    public void setMaxRows(int max) throws SQLException {
        // TODO: implement
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    public void setQueryTimeout(int seconds) throws SQLException {

    }

    public void cancel() throws SQLException {
        SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.cancel()");
    }

    public void setCursorName(String name) throws SQLException {
        // No-op: "If the database does not support positioned update/delete, this method is a noop"
    }

    public boolean isPoolable() throws SQLException {
        return false;
    }

    public void closeOnCompletion() throws SQLException {
        // TODO: implement
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return false; // TODO: implement
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public void addBatch(String sql) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.addBatch(String)");
    }

    public void clearBatch() throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.clearBatch()");
    }

    public int[] executeBatch() throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.executeBatch()");
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.executeUpdate(String, int)");
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.executeUpdate(String, int[])");
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.executeUpdate(String, String[])");
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.execute(String, int)");
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.execute(String, int[])");
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw SQLError.JDBC_FUNCTION_NOT_SUPPORTED.raiseException(
                "java.sql.Statement.execute(String, String[])");
    }



}
