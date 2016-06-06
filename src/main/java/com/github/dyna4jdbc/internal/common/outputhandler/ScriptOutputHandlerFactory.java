package com.github.dyna4jdbc.internal.common.outputhandler;

import java.sql.Statement;

public interface ScriptOutputHandlerFactory {

    SingleResultSetScriptOutputHandler
        newSingleResultSetQueryScriptOutputHandler(Statement statement, String script, SQLWarningSink warningSink);

    MultipleResultSetScriptOutputHandler
        newUpdateOrQueryScriptOutputHandler(Statement statement, String script, SQLWarningSink warningSink);

    UpdateScriptOutputHandler
        newUpdateScriptOutputHandler(Statement statement, String script, SQLWarningSink warningSink);
}
