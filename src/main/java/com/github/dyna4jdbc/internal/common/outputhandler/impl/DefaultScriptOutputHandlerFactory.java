package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.MultiTypeScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public final class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {

    // TODO: cleanup

    private final TypeHandlerFactory typeHandlerFactory;
    private final Configuration configuration;

    public DefaultScriptOutputHandlerFactory(TypeHandlerFactory typeHandlerFactory, Configuration configuration) {
        this.typeHandlerFactory = typeHandlerFactory;
        this.configuration = configuration;

    }

    @Override
    public SingleResultSetScriptOutputHandler newSingleResultSetScriptOutputHandler(
            Statement statement, String script) {

        return new DefaultResultSetScriptOutputHandler(statement, typeHandlerFactory, configuration);
    }

    @Override
    public MultiTypeScriptOutputHandler newMultiTypeScriptOutputHandler(
            Statement statement, String script) {

        return new DefaultResultSetScriptOutputHandler(statement, typeHandlerFactory, configuration);
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(
            Statement statement, String script) {

        return new DefaultUpdateScriptOutputHandler();
    }

    private static final class DefaultUpdateScriptOutputHandler implements UpdateScriptOutputHandler {
        @Override
        public int getUpdateCount() {
            return 0;
        }

        @Override
        public OutputStream getOutOutputStream() {
            return new DisallowAllWritesOutputStream("Writing to to stdout from update is not allowed");
        }

        @Override
        public OutputStream getErrorOutputStream() {
            return null;
        }
    }

    private static class DefaultResultSetScriptOutputHandler
            implements SingleResultSetScriptOutputHandler, MultiTypeScriptOutputHandler {

        private final Statement statement;
        private final DataTableWriter stdOut;
        private final TypeHandlerFactory typeHandlerFactory;

        public DefaultResultSetScriptOutputHandler(Statement statement, TypeHandlerFactory typeHandlerFactory, Configuration configuration) {
            this.statement = statement;
            this.typeHandlerFactory = typeHandlerFactory;

            this.stdOut = new DataTableWriter(configuration);
        }

        private List<ResultSet> processObjectListToResultSet() {

            return stdOut.getDataTableList().stream()
                    .map(dataTable -> new DataTableHolderResultSet(statement, dataTable, typeHandlerFactory))
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

                throw JDBCError.RESULT_SET_MULTIPLE_EXPECTED_ONE.raiseSQLException(resultSets.size());

            } else {
                return resultSets.get(0);
            }

        }

        @Override
        public OutputStream getOutOutputStream() {
            return stdOut;
        }

        @Override
        public OutputStream getErrorOutputStream() {
            return null;
        }
    }
}
