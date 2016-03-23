package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.script.ScriptException;

import com.github.dyna4jdbc.internal.common.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;

class ScriptEngineStatement extends AbstractScriptEngineStatement {

    private final ScriptOutputHandlerFactory scriptOutputHandlerFactory;

    public ScriptEngineStatement(ScriptEngineConnection scriptEngineConnection,
                                 ScriptOutputHandlerFactory scriptOutputHandlerFactory) {

        super(scriptEngineConnection);
        this.scriptOutputHandlerFactory = scriptOutputHandlerFactory;
    }

    protected ResultSet executeScriptForSingleResultSet(String script) throws ScriptException, SQLException {

        SingleResultSetScriptOutputHandler outputHandler =
                scriptOutputHandlerFactory.newSingleResultSetScriptOutputHandler(this, script);

        executeScriptUsingOutputHandler(script, outputHandler);

        return outputHandler.getResultSet();
    }

    protected int executeScriptForUpdateCount(String script) throws ScriptException {

        UpdateScriptOutputHandler outputHandler =
                scriptOutputHandlerFactory.newUpdateScriptOutputHandler(this, script);

        executeScriptUsingOutputHandler(script, outputHandler);

        return outputHandler.getUpdateCount();
    }

    @Override
    protected boolean executeScript(String script, ScriptResultHandler scriptResultHandler) throws ScriptException {

        MultiTypeScriptOutputHandler outputHandler =
                scriptOutputHandlerFactory.newMultiTypeScriptOutputHandler(this, script);

        executeScriptUsingOutputHandler(script, outputHandler);

        boolean resultSets = outputHandler.isResultSets();
        if(resultSets) {
            List<ResultSet> resultSetList = outputHandler.getResultSets();
            scriptResultHandler.onResultSets(resultSetList);

        } else {
            int updateCount = outputHandler.getUpdateCount();
            scriptResultHandler.onUpdateCount(updateCount);
        }

        return resultSets;
    }

    private void executeScriptUsingOutputHandler(
            String script, ScriptOutputHandler scriptOutputHandler) throws ScriptException {

        PrintWriter outPrintWriter = scriptOutputHandler.getOutPrintWriter();
        PrintWriter errorPrintWriter = scriptOutputHandler.getErrorPrintWriter();

        executeScriptUsingCustomWriters(script, outPrintWriter, errorPrintWriter);

        if(outPrintWriter != null) {
            outPrintWriter.flush();
            outPrintWriter.close();
        }
    }
}
