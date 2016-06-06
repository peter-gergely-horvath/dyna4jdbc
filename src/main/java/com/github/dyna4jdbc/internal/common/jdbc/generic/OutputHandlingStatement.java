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
import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.AbortedError;
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
            return executeQueryInternal(script);

        } catch (ScriptExecutionException se) {
            String message = ExceptionUtils.getRootCauseMessage(se);
            throw JDBCError.SCRIPT_EXECUTION_EXCEPTION.raiseSQLException(se, message);
        } catch (SQLException sqle) {
            throw sqle;
        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);
        } catch (AbortedError e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            throw JDBCError.EXECUTION_ABORTED_AT_CLIENT_REQUEST.raiseSQLException(e, message);
        } catch (Throwable t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(t, message);
        }
    }

    private ResultSet executeQueryInternal(String script)
            throws ScriptExecutionException, IOException, SQLException {

        SingleResultSetScriptOutputHandler outputHandler =
                scriptOutputHandlerFactory.newSingleResultSetQueryScriptOutputHandler(this, script, warningSink);

        executeScriptUsingOutputHandler(script, outputHandler);

        ResultSet resultSet = outputHandler.getResultSet();

        registerAsChild(resultSet);

        return resultSet;
    }

    public final int executeUpdate(final String script) throws SQLException {
        checkNotClosed();

        try {
            return executeUpdateInternal(script);

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
        } catch (AbortedError e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            throw JDBCError.EXECUTION_ABORTED_AT_CLIENT_REQUEST.raiseSQLException(e, message);
        } catch (Throwable t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(t, message);
        }
    }

    private int executeUpdateInternal(String script)
            throws ScriptExecutionException, IOException {

        UpdateScriptOutputHandler outputHandler =
                scriptOutputHandlerFactory.newUpdateScriptOutputHandler(this, script, warningSink);

        executeScriptUsingOutputHandler(script, outputHandler);

        return ZERO_UPDATE_COUNT;
    }

    public final boolean execute(final String script) throws SQLException {
        checkNotClosed();

        try {
            return executeInternal(script);

        } catch (ScriptExecutionException se) {
            String message = ExceptionUtils.getRootCauseMessage(se);
            throw JDBCError.SCRIPT_EXECUTION_EXCEPTION.raiseSQLException(se, message);
        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);
        } catch (AbortedError e) {
            String message = ExceptionUtils.getRootCauseMessage(e);
            throw JDBCError.EXECUTION_ABORTED_AT_CLIENT_REQUEST.raiseSQLException(e, message);
        } catch (Throwable t) {
            String message = ExceptionUtils.getRootCauseMessage(t);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(t, message);
        }
    }

    private boolean executeInternal(String script)
            throws ScriptExecutionException, IOException, SQLException {

        MultipleResultSetScriptOutputHandler outputHandler =
                scriptOutputHandlerFactory.newUpdateOrQueryScriptOutputHandler(this, script, warningSink);

        executeScriptUsingOutputHandler(script, outputHandler);

        List<ResultSet> resultSetList = outputHandler.getResultSets();
        registerAsChildren(resultSetList);

        setCurrentResultSetList(resultSetList);
        setUpdateCount(INVALID_UPDATE_COUNT);

        return true;
    }


    private void executeScriptUsingOutputHandler(
            String script, ScriptOutputHandler scriptOutputHandler) throws ScriptExecutionException, IOException {

        OutputStream outOutputStream = scriptOutputHandler.getOutOutputStream();
        OutputStream errorOutputStream = scriptOutputHandler.getErrorOutputStream();

        outputCapturingScriptExecutor.executeScriptUsingStreams(script, outOutputStream, errorOutputStream);
    }

    @Override
    public final void cancel() throws SQLException {
        try {
            outputCapturingScriptExecutor.cancel();

        } catch (CancelException ce) {

            Throwable rootCause = ExceptionUtils.getRootCause(ce);
            if (rootCause instanceof SQLException) {
                throw (SQLException) rootCause;
            } else {
                throw JDBCError.CANCEL_FAILED.raiseSQLException(
                        rootCause, rootCause.getMessage());
            }


        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);

        } catch (Exception ex) {
            throw JDBCError.CANCEL_FAILED.raiseSQLException(
                    ex, ex.getMessage());
        }
    }
}
