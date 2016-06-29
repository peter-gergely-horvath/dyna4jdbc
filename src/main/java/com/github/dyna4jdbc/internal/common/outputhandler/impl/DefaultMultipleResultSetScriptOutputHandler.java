package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.MultipleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Peter Horvath
 */
final class DefaultMultipleResultSetScriptOutputHandler implements MultipleResultSetScriptOutputHandler {

    private static final int NO_BOUNDS_MAX_ROWS = 0;

    private final Statement statement;
    private final DataTableWriter stdOut;
    private final OutputStream stdErr;
    private final ColumnHandlerFactory columnHandlerFactory;

    DefaultMultipleResultSetScriptOutputHandler(
            Statement statement,
            ColumnHandlerFactory columnHandlerFactory,
            Configuration configuration, SQLWarningSink warningSink, int maxRows) {

        this.statement = statement;
        this.columnHandlerFactory = columnHandlerFactory;

        if (maxRows != NO_BOUNDS_MAX_ROWS) {

            final int maxRowsIncludingHeaders = maxRows + 1;

            this.stdOut = new BoundedDataTableWriter(configuration, maxRowsIncludingHeaders);
        } else {
            this.stdOut = new DataTableWriter(configuration);
        }

        this.stdErr = new SQLWarningSinkOutputStream(configuration, warningSink);
    }

    @Override
    public List<ResultSet> getResultSets() {

        return stdOut.getDataTableList().stream()
                .map(dataTable -> new DataTableHolderResultSet(statement, dataTable, columnHandlerFactory))
                .collect(Collectors.toList());
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
