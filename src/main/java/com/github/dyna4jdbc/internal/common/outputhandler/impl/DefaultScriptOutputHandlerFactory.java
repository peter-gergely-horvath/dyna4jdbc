package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {

    // TODO: cleanup

    public DefaultScriptOutputHandlerFactory() {
    }

    private static class DefaultResultSetScriptOutputHandler
            implements SingleResultSetScriptOutputHandler, MultiTypeScriptOutputHandler {

        private final DataTableWriter stdOut = new DataTableWriter();
        private final PrintWriter printWriter = new PrintWriter(stdOut);


        private List<ResultSet> processObjectListToResultSet() {

            DataTable collectedDataTable = stdOut.getDataTable();

            if (collectedDataTable.getAllRowsAreOfSameLength()) {
                return Arrays.asList(new DataTableHolderResultSet(collectedDataTable));
            } else {

                return collectedDataTable.partitionByRowLengthDifferences().stream()
                        .map(dataTable -> new DataTableHolderResultSet(dataTable))
                        .collect(Collectors.toList());
            }


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
        public ResultSet getResultSet() {
            try {
                List<ResultSet> resultSets = getResultSets();

                if (resultSets.size() > 1) {

                    throw SQLError.RESULT_SET_MULTIPLE_EXPECTED_ONE.raiseException(resultSets.size());

                } else {
                    return resultSets.get(0);
                }
            } catch (SQLException e) {
                throw new IllegalStateException(e);
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
