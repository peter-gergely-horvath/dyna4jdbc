package com.github.dyna4jdbc.internal.scriptengine.outputhandler;

import java.sql.ResultSet;

public interface SingleResultSetScriptOutputHandler extends ScriptOutputHandler {
    ResultSet getResultSet();
}
