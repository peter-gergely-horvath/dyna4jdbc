package com.github.dyna4jdbc.internal.scriptengine.outputhandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SingleResultSetScriptOutputHandler extends ScriptOutputHandler {
    ResultSet getResultSet();
}
