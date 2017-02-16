/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableAdapterResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.SingleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.io.SQLWarningSinkOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Peter G. Horvath
 */
final class DefaultSingleResultSetScriptOutputHandler implements SingleResultSetScriptOutputHandler {

    private final Statement statement;
    private final DataTableWriter stdOut;
    private final OutputStream stdErr;
    private final ColumnHandlerFactory columnHandlerFactory;

    DefaultSingleResultSetScriptOutputHandler(
            Statement statement,
            ColumnHandlerFactory columnHandlerFactory,
            Configuration configuration, SQLWarningSink warningSink, int maxRows) {

        this.statement = statement;
        this.columnHandlerFactory = columnHandlerFactory;

        if (maxRows != ResultSetScriptOutputHandlerConstants.MAX_ROWS_NO_BOUNDS) {
            final int maxRowsIncludingHeaders = maxRows + 1;

            this.stdOut = new BoundedDataTableWriter(configuration, maxRowsIncludingHeaders);
        } else {
            this.stdOut = new DataTableWriter(configuration);
        }
        this.stdErr = new SQLWarningSinkOutputStream(configuration, warningSink);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {

        List<DataTable> dataTableList = stdOut.getDataTableList();

        DataTable singleDataTable;
        switch (dataTableList.size()) {
            case 0:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
                        "dataTableList is empty");

            case 1:
                singleDataTable = dataTableList.get(0);
                break;

            default:
                throw JDBCError.RESULT_SET_MULTIPLE_EXPECTED_ONE.raiseSQLException(
                        "Expected one ResultSet, script generated multiple: " + dataTableList.size());
        }

        return new DataTableAdapterResultSet(statement, singleDataTable, columnHandlerFactory);
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
