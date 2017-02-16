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

import com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableAdapterResultSet;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.MultipleResultSetScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.io.SQLWarningSinkOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Peter G. Horvath
 */
final class DefaultMultipleResultSetScriptOutputHandler implements MultipleResultSetScriptOutputHandler {

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

        if (maxRows != ResultSetScriptOutputHandlerConstants.MAX_ROWS_NO_BOUNDS) {

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
                .map(dataTable -> new DataTableAdapterResultSet(statement, dataTable, columnHandlerFactory))
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
