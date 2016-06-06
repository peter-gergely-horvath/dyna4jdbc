package com.github.dyna4jdbc.internal.common.outputhandler;


import java.sql.ResultSet;
import java.util.List;

public interface MultipleResultSetScriptOutputHandler extends ScriptOutputHandler {

    List<ResultSet> getResultSets();
}
