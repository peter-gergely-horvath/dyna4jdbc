package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.OutputDisabledError;
import com.github.dyna4jdbc.internal.RuntimeDyna4JdbcException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.*;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtils;

public class OutputHandlingStatement<T extends java.sql.Connection> extends AbstractStatement<T> {

    private final ScriptOutputHandlerFactory scriptOutputHandlerFactory;
    private final OutputCapturingScriptExecutor outputCapturingScriptExecutor;

    private final SQLWarningSink warningSink = new SQLWarningSink() {
        @Override
        public void onSQLWarning(SQLWarning warning) {
            addSQLWarning(warning);
        }
    };

    public OutputHandlingStatement(
            T connection,
            ScriptOutputHandlerFactory scriptOutputHandlerFactory,
            OutputCapturingScriptExecutor outputCapturingScriptExecutor) {

        super(connection);
        this.scriptOutputHandlerFactory = scriptOutputHandlerFactory;
        this.outputCapturingScriptExecutor = outputCapturingScriptExecutor;
    }

    public final ResultSet executeQuery(String script) throws SQLException {
        checkNotClosed();

        try {
            SingleResultSetQueryScriptOutputHandler outputHandler =
                    scriptOutputHandlerFactory.newSingleResultSetQueryScriptOutputHandler(this, script, warningSink);

            executeScriptUsingOutputHandler(script, outputHandler);

            ResultSet resultSet = outputHandler.getResultSet();

            registerAsChild(resultSet);

            return resultSet;

        } catch (ScriptExecutionException se) {
            String message = ExceptionUtils.getRootCauseMessage(se);
            throw JDBCError.SCRIPT_EXECUTION_EXCEPTION.raiseSQLException(se, message);
        } catch (SQLException sqle) {
            throw sqle;
        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);
        } catch (Throwable t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(t, message);
        }
    }

    public final int executeUpdate(final String script) throws SQLException {
        checkNotClosed();

        try {
            UpdateScriptOutputHandler outputHandler =
                    scriptOutputHandlerFactory.newUpdateScriptOutputHandler(this, script, warningSink);

            executeScriptUsingOutputHandler(script, outputHandler);

            return outputHandler.getUpdateCount();

        } catch (ScriptExecutionException se) {

            Throwable rootCause = ExceptionUtils.getRootCause(se);
            if (rootCause instanceof OutputDisabledError) {
                throw JDBCError.USING_STDOUT_FROM_UPDATE.raiseSQLException(rootCause, rootCause.getMessage());

            } else {
                String message = rootCause.getMessage();
                throw JDBCError.SCRIPT_EXECUTION_EXCEPTION.raiseSQLException(se, message);
            }
        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);
        } catch (OutputDisabledError t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.USING_STDOUT_FROM_UPDATE.raiseSQLException(t, message);
        } catch (Throwable t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(t, message);
        }
    }

    public final boolean execute(final String script) throws SQLException {
        checkNotClosed();

        try {

            UpdateOrQueryScriptOutputHandler outputHandler =
                    scriptOutputHandlerFactory.newUpdateOrQueryScriptOutputHandler(this, script, warningSink);

            executeScriptUsingOutputHandler(script, outputHandler);

            boolean resultSets = outputHandler.isResultSets();
            if (resultSets) {
                List<ResultSet> resultSetList = outputHandler.getResultSets();
                registerAsChildren(resultSetList);

                setCurrentResultSetList(resultSetList);
                setUpdateCount(-1);

            } else {
                int updateCount = outputHandler.getUpdateCount();
                setUpdateCount(updateCount);
            }

            return resultSets;
        } catch (ScriptExecutionException se) {
            String message = ExceptionUtils.getRootCauseMessage(se);
            throw JDBCError.SCRIPT_EXECUTION_EXCEPTION.raiseSQLException(se, message);
        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);
        } catch (Throwable t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(t, message);
        }
    }


    private void executeScriptUsingOutputHandler(
            String script, ScriptOutputHandler scriptOutputHandler) throws ScriptExecutionException, IOException {

        OutputStream outOutputStream = scriptOutputHandler.getOutOutputStream();
        OutputStream errorOutputStream = scriptOutputHandler.getErrorOutputStream();

        executeScriptUsingCustomWriters(script, outOutputStream, errorOutputStream);

        if (outOutputStream != null) {
            outOutputStream.flush();
            outOutputStream.close();
        }

        if (errorOutputStream != null) {
            errorOutputStream.flush();
            errorOutputStream.close();
        }
    }

    private void executeScriptUsingCustomWriters(String script, OutputStream outWriter, OutputStream errorWriter)
            throws ScriptExecutionException {


        outputCapturingScriptExecutor.executeScriptUsingStreams(script, outWriter, errorWriter);

    }
}
