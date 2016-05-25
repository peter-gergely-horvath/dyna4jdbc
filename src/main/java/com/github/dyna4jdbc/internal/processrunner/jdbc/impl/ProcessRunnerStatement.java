package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;

class ProcessRunnerStatement extends OutputHandlingStatement<ProcessRunnerConnection> {

    private final ProcessRunnerScriptExecutor scriptExecutor;

    ProcessRunnerStatement(ProcessRunnerConnection connection,
                           ScriptOutputHandlerFactory scriptOutputHandlerFactory,
                           ProcessRunnerScriptExecutor scriptExecutor) {

        super(connection, scriptOutputHandlerFactory, scriptExecutor);

        this.scriptExecutor = scriptExecutor;
    }

    @Override
    public void cancel() throws SQLException {
        scriptExecutor.close();
    }
}
