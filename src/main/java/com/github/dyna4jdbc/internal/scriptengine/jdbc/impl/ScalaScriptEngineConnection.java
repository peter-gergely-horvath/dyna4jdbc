package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptEngine;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream;
import com.github.dyna4jdbc.internal.common.util.io.CloseSuppressingOutputStream;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream.AbortHandler;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import scala.Console;
import scala.runtime.AbstractFunction0;
import scala.tools.nsc.interpreter.IMain;

public final class ScalaScriptEngineConnection extends DefaultScriptEngineConnection {

    private static final String USEJAVACP_ARGUMENT = "-usejavacp";

    private final IMain scalaInterpreterMain;

    public ScalaScriptEngineConnection(String parameters, Properties properties)
            throws SQLException, MisconfigurationException {
        super(parameters, properties);

        ScriptEngine scriptEngine = this.getEngine();
        if (!(scriptEngine instanceof IMain)) {
            JDBCError.INVALID_CONFIGURATION.raiseSQLException(
                    "Cannot configure Scala ScriptEngine of type: "
                            + scriptEngine.getClass().getName()
                            + ". Are you using a Scala version "
                            + "different from the one the driver was compiled for?");
        }


        this.scalaInterpreterMain = ((IMain) scriptEngine);

        this.scalaInterpreterMain.setContextClassLoader();
        this.scalaInterpreterMain.settings().processArgumentString(USEJAVACP_ARGUMENT);

        registerAsChild(() -> this.scalaInterpreterMain.close());
    }

    @Override
    public void executeScriptUsingAbortableStreams(
            String script, 
            Map<String, Object> variables,
            AbortableOutputStream stdOutputStream, AbortableOutputStream errorOutputStream,
            AbortHandler abortHandler)
            throws ScriptExecutionException {

        IOHandlerFactory ioHandlerFactory = getIoHandlerFactory();

        try (PrintStream outPrintStream = ioHandlerFactory.newPrintStream(
                new CloseSuppressingOutputStream(stdOutputStream));
             PrintStream errorPrintStream = ioHandlerFactory.newPrintStream(
                     new CloseSuppressingOutputStream(errorOutputStream))) {

            Console.withOut(outPrintStream, new AbstractFunction0<Void>() {
                @Override
                public Void apply() {


                    Console.withErr(errorPrintStream, new AbstractFunction0<Void>() {
                        @Override
                        public Void apply() {

                            doExecuteScriptUsingAbortableStreams(
                                    script, variables, stdOutputStream, errorOutputStream, 
                                    abortHandler);

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

    private void doExecuteScriptUsingAbortableStreams(
            String script, 
            Map<String, Object> variables, 
            AbortableOutputStream stdOutputStream,
            AbortableOutputStream errorOutputStream,
            AbortHandler abortHandler) {

        try {

            super.executeScriptUsingAbortableStreams(
                    script,
                    variables,
                    stdOutputStream,
                    errorOutputStream,
                    abortHandler);

        } catch (ScriptExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
