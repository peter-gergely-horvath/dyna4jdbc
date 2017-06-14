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

 
package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.datamodel.DataColumn;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.base.RowListResultSet;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;

import java.sql.*;
import java.util.*;

public final class DataTableAdapterResultSet extends RowListResultSet<List<String>> {

    private final int columnCount;

    public DataTableAdapterResultSet(
            Statement statement, DataTable dataTable, ColumnHandlerFactory columnHandlerFactory) {

        super(dataTable.getRows(), statement, initColumnHandlers(dataTable, columnHandlerFactory));
        columnCount = dataTable.getColumnCount();

        if (checkFirstRowIsSkipped(getColumnHandlers())) {
            super.skipNextRowIfPresent();
        }

        registerAsChild(dataTable);
    }

    private static List<ColumnHandler> initColumnHandlers(DataTable dataTable,
                                                        ColumnHandlerFactory columnHandlerFactory) {

        LinkedList<ColumnHandler> columnHandlerList = new LinkedList<>();

        int columnIndex = 0;

        for (DataColumn column : dataTable.columnIterable()) {
            ColumnHandler columnHandler = columnHandlerFactory.newColumnHandler(columnIndex++, column);
            if (columnHandler == null) {
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "columnHandler is null");
            }

            columnHandlerList.add(columnHandler);
        }

        return Collections.unmodifiableList(columnHandlerList);
    }

    private static boolean checkFirstRowIsSkipped(List<ColumnHandler> columnHandlers) {

        Boolean shouldConsumeFirstRow = null;

        final int typeHandlerNumber = columnHandlers.size();
        for (int i = 0; i < typeHandlerNumber; i++) {

            ColumnHandler columnHandler = columnHandlers.get(i);

            final boolean thisTypeHandlerConsumesFirstRowValue =
                    columnHandler.getColumnMetadata().isConsumesFirstRow();

            if (shouldConsumeFirstRow == null) {
                shouldConsumeFirstRow = thisTypeHandlerConsumesFirstRowValue;
            } else {
                if (shouldConsumeFirstRow != thisTypeHandlerConsumesFirstRowValue) {

                    final int sqlIndexOfColumn = i + 1;

                    throw JDBCError.INCONSISTENT_HEADER_SPECIFICATION.raiseUncheckedException(
                            String.format(
                                    "Column specification for column %s is invalid / inconsistent: "
                                    + "ensure SQL type is valid and that explicit declaration / "
                                    + "heuristic detection is not mixed. "
                                    + "(HINT: Isn't there any typo or unintended column "
                                    + "separator in the header line?)", sqlIndexOfColumn));
                }
            }
        }

        if (shouldConsumeFirstRow == null) {
            shouldConsumeFirstRow = false;
        }

        return shouldConsumeFirstRow;
    }

    protected String getRawCellValueBySqlColumnIndex(int sqlColumnIndex) throws SQLException {

        final int javaIndex = sqlColumnIndex - 1;

        List<String> currentRow = getCurrentRow();

        if (!(javaIndex >= 0 && javaIndex < columnCount)) {
            throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException(
                    "Invalid index: " + sqlColumnIndex);
        }

        String cellValue;
        if (javaIndex < currentRow.size()) {
            cellValue = currentRow.get(javaIndex);
        } else {
            cellValue = null;
        }

        return cellValue;
    }

    @Override
    public String getCursorName() throws SQLException {
        checkNotClosed();

        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Retrieval of cursor name");
    }
}
