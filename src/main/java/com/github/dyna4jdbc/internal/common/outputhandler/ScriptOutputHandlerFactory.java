package com.github.dyna4jdbc.internal.common.outputhandler;

public interface ScriptOutputHandlerFactory {

    SingleResultSetScriptOutputHandler
        newSingleResultSetScriptOutputHandler(java.sql.Statement statement, String script);

    MultiTypeScriptOutputHandler
        newMultiTypeScriptOutputHandler(java.sql.Statement statement, String script);

    UpdateScriptOutputHandler
        newUpdateScriptOutputHandler(java.sql.Statement statement, String script);
}
