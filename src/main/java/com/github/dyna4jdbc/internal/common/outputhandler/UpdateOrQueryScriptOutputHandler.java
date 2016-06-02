package com.github.dyna4jdbc.internal.common.outputhandler;


import java.sql.ResultSet;
import java.util.List;

public interface UpdateOrQueryScriptOutputHandler extends ScriptOutputHandler {

    boolean isResultSets();
    List<ResultSet> getResultSets();
    int getUpdateCount();
}
