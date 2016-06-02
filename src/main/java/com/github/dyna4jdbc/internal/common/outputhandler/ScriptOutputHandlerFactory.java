package com.github.dyna4jdbc.internal.common.outputhandler;

import java.sql.Statement;

public interface ScriptOutputHandlerFactory {

    SingleResultSetQueryScriptOutputHandler
        newSingleResultSetQueryScriptOutputHandler(Statement statement, String script, SQLWarningSink warningSink);

    UpdateOrQueryScriptOutputHandler
        newUpdateOrQueryScriptOutputHandler(Statement statement, String script, SQLWarningSink warningSink);

    UpdateScriptOutputHandler
        newUpdateScriptOutputHandler(Statement statement, String script, SQLWarningSink warningSink);
}
