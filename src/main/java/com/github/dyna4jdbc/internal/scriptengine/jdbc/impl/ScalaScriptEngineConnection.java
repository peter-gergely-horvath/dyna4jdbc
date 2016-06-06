package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.CloseSuppressingOutputStream;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import scala.Console;
import scala.runtime.AbstractFunction0;
import scala.tools.nsc.interpreter.IMain;

import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Properties;

public final class ScalaScriptEngineConnection extends DefaultScriptEngineConnection {

    private static final String USEJAVACP_ARGUMENT = "-usejavacp";

    private final IMain scalaInterpreterMain;

    public ScalaScriptEngineConnection(String parameters, Properties properties)
            throws SQLException, MisconfigurationException {
        super(parameters, properties);

        if (!(this.getEngine() instanceof IMain)) {
            JDBCError.INVALID_CONFIGURATION.raiseSQLException(
                    "Cannot configure Scala ScriptEngine of type: "
                            + this.getEngine().getClass().getName()
                            + ". Are you using a Scala version "
                            + "different from the one the driver was compiled for?");
        }


        this.scalaInterpreterMain = ((IMain) this.getEngine());

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
    public void executeScriptUsingStreams(
            String script, OutputStream stdOutputStream, OutputStream errorOutputStream)
            throws ScriptExecutionException {

        IOHandlerFactory ioHandlerFactory = getIoHandlerFactory();

        // TODO: abort handling??

        try (PrintStream outPrintStream = ioHandlerFactory.newPrintStream(stdOutputStream);
             PrintStream errorPrintStream = ioHandlerFactory.newPrintStream(errorOutputStream)) {

            Console.withOut(outPrintStream, new AbstractFunction0<Void>() {
                @Override
                public Void apply() {


                    Console.withErr(errorPrintStream, new AbstractFunction0<Void>() {
                        @Override
                        public Void apply() {

                            doExecuteScriptUsingStreams(script, stdOutputStream, errorOutputStream);

                            return null;
                        }
                    });

                    return null;
                }
            });

        } catch (RuntimeException re) {
            Throwable cause = re.getCause();
            if (cause instanceof ScriptExecutionException) {
                throw (ScriptExecutionException) cause;
            } else {
                throw re;
            }
        }

    }

    private void doExecuteScriptUsingStreams(
            String script, OutputStream stdOutputStream, OutputStream errorOutputStream) {
        try {

            super.executeScriptUsingStreams(script,
                    new CloseSuppressingOutputStream(stdOutputStream),
                    new CloseSuppressingOutputStream(errorOutputStream));

        } catch (ScriptExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
