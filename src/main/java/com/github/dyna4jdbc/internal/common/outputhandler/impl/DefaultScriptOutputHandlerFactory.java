package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.*;
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
    public SingleResultSetQueryScriptOutputHandler newSingleResultSetQueryScriptOutputHandler(
            Statement statement, String script, SQLWarningSink warningSink) {

        return new DefaultResultSetQueryScriptOutputHandler(
                statement, typeHandlerFactory, configuration, warningSink);
    }

    @Override
    public UpdateOrQueryScriptOutputHandler newUpdateOrQueryScriptOutputHandler(
            Statement statement, String script, SQLWarningSink warningSink) {

        return new DefaultResultSetQueryScriptOutputHandler(
                statement, typeHandlerFactory, configuration, warningSink);
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(
            Statement statement, String script, SQLWarningSink warningSink) {

        return new DefaultUpdateScriptOutputHandler(this.configuration, warningSink);
    }

    private static final class DefaultUpdateScriptOutputHandler implements UpdateScriptOutputHandler {

        private final SQLWarningSinkOutputStream stdErr;

        private DefaultUpdateScriptOutputHandler(Configuration configuration, SQLWarningSink warningSink) {

            this.stdErr = new SQLWarningSinkOutputStream(configuration, warningSink);
        }

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
            return stdErr;
        }
    }

    private static final class DefaultResultSetQueryScriptOutputHandler
            implements SingleResultSetQueryScriptOutputHandler, UpdateOrQueryScriptOutputHandler {

        private final Statement statement;
        private final DataTableWriter stdOut;
        private final OutputStream stdErr;
        private final TypeHandlerFactory typeHandlerFactory;

        private DefaultResultSetQueryScriptOutputHandler(
                Statement statement,
                TypeHandlerFactory typeHandlerFactory,
                Configuration configuration, SQLWarningSink warningSink) {

            this.statement = statement;
            this.typeHandlerFactory = typeHandlerFactory;

            this.stdOut = new DataTableWriter(configuration);
            this.stdErr = new SQLWarningSinkOutputStream(configuration, warningSink);
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
            return this.stdOut;
        }

        @Override
        public OutputStream getErrorOutputStream() {
            return this.stdErr;
        }
    }
}
