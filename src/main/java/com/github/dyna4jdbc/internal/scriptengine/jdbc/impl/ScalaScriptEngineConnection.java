package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import scala.Console;
import scala.Function0;
import scala.runtime.AbstractFunction0;
import scala.tools.nsc.interpreter.IMain;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Properties;

public final class ScalaScriptEngineConnection extends ScriptEngineConnection {

    private static final String USEJAVACP_ARGUMENT = "-usejavacp";

    private final IMain scalaInterpreterMain;

    public ScalaScriptEngineConnection(String parameters, Properties properties)
            throws SQLException, MisconfigurationException {
        super(parameters, properties);

        if(!(this.engine instanceof IMain)) {
            JDBCError.INVALID_CONFIGURATION.raiseSQLException(
                    "Cannot configure Scala ScriptEngine of type: " + this.engine.getClass().getName() +
                            ". Are you using a Scala version different from the one the driver was compiled for?" );
        }


        this.scalaInterpreterMain = ((IMain)this.engine);

        this.scalaInterpreterMain.setContextClassLoader();
        this.scalaInterpreterMain.settings().processArgumentString(USEJAVACP_ARGUMENT);
    }

    @Override
    protected void closeInternal() throws SQLException {

        try {
            this.scalaInterpreterMain.close();
        } catch (Throwable e) {
            JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(e, "Closing Scala ScriptEngine failed");
        }

        super.closeInternal();
    }

    @Override
    public void executeScriptUsingCustomWriters(
            String script, OutputStream stdOutputStream, OutputStream errorOutputStream)
            throws ScriptExecutionException {

        if (stdOutputStream == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("stdOutputStream is null");
        }

        try {

            Function0<Void> invokerFunction =
                    createOutputCapturingInvokerFunction(script, stdOutputStream, errorOutputStream);

            if (errorOutputStream != null) {

                invokerFunction =
                        createErrorCapturingInvokerFunction(errorOutputStream, invokerFunction);
            }

            Console.withOut(ioHandlerFactory.newPrintStream(stdOutputStream), invokerFunction);

        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            if (cause instanceof ScriptExecutionException) {
                throw (ScriptExecutionException) cause;
            } else {
                throw re;
            }
        }

    }

    private Function0<Void> createErrorCapturingInvokerFunction(
            final OutputStream errorOutputStream, final Function0<Void> executeScriptFunction) {

        return new AbstractFunction0<Void>() {
            @Override
            public Void apply() {

                Console.withErr(ioHandlerFactory.newPrintStream(errorOutputStream), executeScriptFunction);

                return null;
            }
        };
    }

    private Function0<Void> createOutputCapturingInvokerFunction(
            String script, OutputStream stdOutputStream, OutputStream errorOutputStream) {

        return new AbstractFunction0<Void>() {
            @Override
            public Void apply() {

                doExecuteScriptUsingCustomWriters(script, stdOutputStream, errorOutputStream);

                return null;
            }
        };

    }

    private void doExecuteScriptUsingCustomWriters(
            String script, OutputStream stdOutputStream, OutputStream errorOutputStream) {
        try {

            super.executeScriptUsingCustomWriters(script, stdOutputStream, errorOutputStream);

        } catch (ScriptExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
