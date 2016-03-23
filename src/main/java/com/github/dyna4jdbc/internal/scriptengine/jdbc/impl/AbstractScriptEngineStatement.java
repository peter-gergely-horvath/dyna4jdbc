package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.OutputDisabledError;
import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractStatement;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtil;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Writer;
import java.sql.*;
import java.util.List;

abstract class AbstractScriptEngineStatement extends AbstractStatement<ScriptEngineConnection> {

    AbstractScriptEngineStatement(ScriptEngineConnection scriptEngineConnection) {
        super(scriptEngineConnection);
    }

    public final ResultSet executeQuery(String script) throws SQLException {
        try {
            return executeScriptForSingleResultSet(script);
        } catch (ScriptException se) {
        	String message = ExceptionUtil.getRootCauseMessage(se);
            throw SQLError.SCRIPT_EXECUTION_EXCEPTION.raiseException(se, message);
        } catch (SQLException sqle) {
            throw sqle;
        } catch (Throwable t) {
        	String message = ExceptionUtil.getRootCauseMessage(t);
            throw SQLError.UNEXPECTED_THROWABLE.raiseException(t, message);
        }
    }

    protected abstract ResultSet executeScriptForSingleResultSet(String script) throws ScriptException, SQLException;

    public final int executeUpdate(final String script) throws SQLException {

        try {
            return executeScriptForUpdateCount(script);

        } catch (ScriptException se) {
        	String message = ExceptionUtil.getRootCauseMessage(se);
            throw SQLError.SCRIPT_EXECUTION_EXCEPTION.raiseException(se, message);
        }
        catch (OutputDisabledError t) {
        	String message = ExceptionUtil.getRootCauseMessage(t);
        	throw SQLError.USING_STDOUT_FROM_UPDATE.raiseException(t, message);
        }
        catch (Throwable t) {
        	String message = ExceptionUtil.getRootCauseMessage(t);
            throw SQLError.UNEXPECTED_THROWABLE.raiseException(t, message);
        }
    }

    protected abstract int executeScriptForUpdateCount(String script) throws ScriptException;

    public final boolean execute(final String script) throws SQLException {

        try {
            return executeScript(script, new ScriptResultHandler());
        } catch (ScriptException se) {
        	String message = ExceptionUtil.getRootCauseMessage(se);
            throw SQLError.SCRIPT_EXECUTION_EXCEPTION.raiseException(se, message);
        } catch (Throwable t) {
        	String message = ExceptionUtil.getRootCauseMessage(t);
            throw SQLError.UNEXPECTED_THROWABLE.raiseException(t, message);
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

    protected void executeScriptUsingCustomWriters(final String script, Writer outWriter, Writer errorWriter)
            throws ScriptException {

        ExecuteScriptUsingCustomPrintWriter customWritersCallback =
                new ExecuteScriptUsingCustomPrintWriter(script, outWriter, errorWriter);

        connection.executeUsingScriptEngine(customWritersCallback);
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
}
