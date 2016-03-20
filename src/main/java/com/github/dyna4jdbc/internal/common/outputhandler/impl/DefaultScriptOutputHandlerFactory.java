package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.OutputDisabledError;
import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {

    // TODO: cleanup

    private final TypeHandlerFactory typeHandlerFactory;

    public DefaultScriptOutputHandlerFactory(TypeHandlerFactory typeHandlerFactory) {
        this.typeHandlerFactory = typeHandlerFactory;

    }

    private static class DefaultResultSetScriptOutputHandler
            implements SingleResultSetScriptOutputHandler, MultiTypeScriptOutputHandler {

        private final DataTableWriter stdOut = new DataTableWriter();
        private final PrintWriter printWriter = new PrintWriter(stdOut);
        private final TypeHandlerFactory typeHandlerFactory;

        public DefaultResultSetScriptOutputHandler(TypeHandlerFactory typeHandlerFactory) {
            this.typeHandlerFactory = typeHandlerFactory;
        }


        private List<ResultSet> processObjectListToResultSet() {

            return stdOut.getDataTableList().stream()
                    .map(dataTable -> new DataTableHolderResultSet(dataTable, typeHandlerFactory))
                    .collect(Collectors.<ResultSet>toList());
        }

        @Override
        public boolean isResultSets() {
            return true;
        }

        @Override
        public List<ResultSet> getResultSets() {
            return processObjectListToResultSet();
        }

        @Override
        public int getUpdateCount() {
            return 0;
        }

        @Override
        public ResultSet getResultSet() throws SQLException {
                List<ResultSet> resultSets = getResultSets();

                if (resultSets.size() > 1) {

                    throw SQLError.RESULT_SET_MULTIPLE_EXPECTED_ONE.raiseException(resultSets.size());

                } else {
                    return resultSets.get(0);
                }

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
        return new DefaultResultSetScriptOutputHandler(typeHandlerFactory);
    }

    @Override
    public MultiTypeScriptOutputHandler newMultiTypeScriptOutputHandler(String script) {
        return new DefaultResultSetScriptOutputHandler(typeHandlerFactory);
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
                return new DisallowAllWritesPrintWriter("Cannot write to stdout from update!");
            }

            @Override
            public PrintWriter getErrorPrintWriter() {
                return null;
            }
        };
    }
}
