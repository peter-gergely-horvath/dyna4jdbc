package com.github.dyna4jdbc.internal.scriptengine.outputhandler.impl;

import com.github.dyna4jdbc.internal.scriptengine.DataTable;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.scriptengine.outputhandler.UpdateScriptOutputHandler;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

public class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {
	
	public DefaultScriptOutputHandlerFactory() {
	}

    private static class DefaultResultSetScriptOutputHandler
            implements SingleResultSetScriptOutputHandler, MultiTypeScriptOutputHandler {

        private final DataTableWriter stdOut = new DataTableWriter();
        private final PrintWriter printWriter = new PrintWriter(stdOut);


        private ResultSet processObjectListToResultSet() {

            DataTable dataTable = stdOut.getDataTable();

            return new DataTableHolderResultSet(dataTable);
        }

        @Override
        public boolean isResultSets() {
            return true;
        }

        @Override
        public List<ResultSet> getResultSets() {
            return Arrays.asList(getResultSet());
        }

        @Override
        public int getUpdateCount() {
            return 0;
        }

        @Override
        public ResultSet getResultSet() {
            return processObjectListToResultSet();
        }

        @Override
        public PrintWriter getOutPrintWriter() {
            return printWriter;
        }

        @Override
        public PrintWriter getErrorPrintWriter() {
            return null;
        }
    }

    @Override
    public SingleResultSetScriptOutputHandler newSingleResultSetScriptOutputHandler(String script) {
        return new DefaultResultSetScriptOutputHandler();
    }

    @Override
    public MultiTypeScriptOutputHandler newMultiTypeScriptOutputHandler(String script) {
        return new DefaultResultSetScriptOutputHandler();
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(String script) {
        return new UpdateScriptOutputHandler() {
            @Override
            public int getUpdateCount() {
                return 0;
            }

            @Override
            public PrintWriter getOutPrintWriter() {
                return DisallowAllWritesPrintWriter.forMessage("Cannot write to stdout from update!");
            }

            @Override
            public PrintWriter getErrorPrintWriter() {
                return null;
            }
        };
    }
}
