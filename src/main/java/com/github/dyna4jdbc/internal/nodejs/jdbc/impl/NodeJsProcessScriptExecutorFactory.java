package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.io.OutputStream;
import java.sql.SQLWarning;
import java.util.LinkedList;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.sqlwarning.SQLWarningCollectorSink;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutorFactory;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ExternalProcessScriptExecutor;


public final class NodeJsProcessScriptExecutorFactory extends DefaultExternalProcessScriptExecutorFactory {

    public static NodeJsProcessScriptExecutorFactory getInstance(String eosToken) {
        return new NodeJsProcessScriptExecutorFactory(eosToken);
    }

    private final String replInitScript;

    private NodeJsProcessScriptExecutorFactory(String eosToken) {
        this.replInitScript = ""
                + "const endOfStreamToken = '" + eosToken + "'; " 
                + "const vm = require('vm'); "
                + "require('repl').start({ " 
                + "terminal: false, " + "prompt: '', " 
                + "ignoreUndefined: true, "
                + "eval: function(cmd, ctx, fn, cb) { " 
                + "try { vm.runInContext(cmd, ctx, fn); } "
                + "catch (err) { cb(err); } "
                + "finally { console.log(endOfStreamToken); console.error(endOfStreamToken); } "
                + "} });";
    }

    @Override
    public ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration) {

        try {
            NodeJsProcessScriptExecutor nodeJsProcessScriptExecutor =
                    new NodeJsProcessScriptExecutor(configuration, replInitScript);

            executeInitScript(configuration, nodeJsProcessScriptExecutor);

            return nodeJsProcessScriptExecutor;

        } catch (ScriptExecutionException e) {
            throw new RuntimeException("Exception initializing NodeJsProcessScriptExecutor", e);
        }

    }

    private void executeInitScript(Configuration configuration, NodeJsProcessScriptExecutor nodeJsProcessScriptExecutor)
            throws ScriptExecutionException {


        IOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);

        SQLWarningCollectorSink sqlWarningSink = new SQLWarningCollectorSink();

        OutputStream outputWarningSinkOutputStream =
            ioHandlerFactory.newWarningSinkOutputStream(sqlWarningSink);
        OutputStream errorWarningSinkOutputStream =
            ioHandlerFactory.newWarningSinkOutputStream(sqlWarningSink);

        nodeJsProcessScriptExecutor.executeScriptUsingStreams("1+1", null,
                outputWarningSinkOutputStream, errorWarningSinkOutputStream);

        LinkedList<SQLWarning> warningList = sqlWarningSink.getWarnings();

        switch (warningList.size()) {
            case 0:
                // NO warnings: OK
                break;

            case 1:
                // ONE warning - use it as root cause
                JDBCError.CONNECT_FAILED_EXCEPTION.raiseUncheckedException(
                        warningList.get(0),
                        "Node.js process wrote to output while starting the REPL: "
                        + "this is considered to be a fatal error.");
                break;

            default:
                // MULTIPLE warnings - use them as suppressed
                JDBCError.CONNECT_FAILED_EXCEPTION.raiseUncheckedExceptionWithSuppressed(warningList,
                        "Node.js process wrote to output while starting the REPL: "
                        + "this is considered to be a fatal error");
        }
    }

}
