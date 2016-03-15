package com.github.dyna4jdbc.internal.common.outputhandler;

import java.sql.ResultSet;

public interface SingleResultSetScriptOutputHandler extends ScriptOutputHandler {
    ResultSet getResultSet();
}
