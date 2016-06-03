package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import org.renjin.primitives.io.connections.ConnectionTable;
import org.renjin.script.RenjinScriptEngine;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;

/**
 * {@code RenjinScriptEngine} does not support adjusting the standard
 * output and standard error via the JSR-223 APIs, so we have to work
 * around this limitation by interacting with Renjin's own API to set
 * custom {@code PrintWriter}s for capturing the R script's output.
 *
 * @author Peter Horvath
 */
public class RenjinScriptEngineConnection extends DefaultScriptEngineConnection {

    private final ConnectionTable renjinConnectionTable;

    public RenjinScriptEngineConnection(String parameters, Properties properties)
            throws SQLException, MisconfigurationException {
        super(parameters, properties);

        if (!(this.getEngine() instanceof RenjinScriptEngine)) {
            JDBCError.INVALID_CONFIGURATION.raiseSQLException(
                    "Cannot configure Renjin ScriptEngine of type: "
                            + this.getEngine().getClass().getName()
                            + ". Are you using a Renjin version "
                            + "different from the one the driver was compiled for?");
        }

        renjinConnectionTable = ((RenjinScriptEngine) this.getEngine()).getSession().getConnectionTable();
    }

    @Override
    public final void executeScriptUsingStreams(
            String script,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        PrintWriter originalStdOut = null;
        PrintWriter originalError = null;

        try {

            originalStdOut = renjinConnectionTable.getStdout().getPrintWriter();
            originalError = renjinConnectionTable.getStderr().getPrintWriter();

            IOHandlerFactory ioHandlerFactory = getIoHandlerFactory();
            if (stdOutputStream != null) {

                PrintWriter outputPrintWriter =
                        ioHandlerFactory.newPrintWriter(stdOutputStream, true);

                renjinConnectionTable.getStdout().setOutputStream(outputPrintWriter);
            }


            if (errorOutputStream != null) {

                PrintWriter errorPrintWriter =
                        ioHandlerFactory.newPrintWriter(errorOutputStream, true);

                renjinConnectionTable.getStderr().setOutputStream(errorPrintWriter);
            }

            super.executeScriptUsingStreams(script, stdOutputStream, errorOutputStream);

        } finally {

            renjinConnectionTable.getStdout().setOutputStream(originalStdOut);
            renjinConnectionTable.getStderr().setOutputStream(originalError);
        }
    }
}
