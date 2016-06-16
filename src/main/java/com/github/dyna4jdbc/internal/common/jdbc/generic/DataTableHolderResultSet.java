package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.datamodel.DataColumn;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.base.DataRowListResultSet;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;

import java.sql.*;
import java.util.*;

public final class DataTableHolderResultSet extends DataRowListResultSet<List<String>> {

    private final DataTable dataTable;

    public DataTableHolderResultSet(
            Statement statement, DataTable dataTable, ColumnHandlerFactory columnHandlerFactory) {

        super(dataTable.getRows(), statement, initTypeHandlers(dataTable, columnHandlerFactory));
        this.dataTable = dataTable;

        if (checkFirstRowIsSkipped(getColumnHandlers())) {
            super.skipNextRowIfPresent();
        }
    }

    private static List<ColumnHandler> initTypeHandlers(DataTable dataTable,
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

        Boolean shouldTakeFirstRowValue = null;

        final int typeHandlerNumber = columnHandlers.size();
        for (int i = 0; i < typeHandlerNumber; i++) {

            ColumnHandler columnHandler = columnHandlers.get(i);

            final boolean thisTypeHandlerTakesFirstRowValue =
                    columnHandler.getColumnMetadata().isConsumesFirstRowValue();

            if (shouldTakeFirstRowValue == null) {
                shouldTakeFirstRowValue = thisTypeHandlerTakesFirstRowValue;
            } else {
                if (shouldTakeFirstRowValue != thisTypeHandlerTakesFirstRowValue) {

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

        if (shouldTakeFirstRowValue == null) {
            shouldTakeFirstRowValue = false;
        }

        return shouldTakeFirstRowValue;
    }

    @Override
    protected void closeInternal() throws SQLException {
        dataTable.clear();
    }

    protected String getRawCellValueBySqlColumnIndex(int sqlColumnIndex) throws SQLException {

        final int javaIndex = sqlColumnIndex - 1;

        List<String> currentRow = getCurrentRow();

        if (!(javaIndex >= 0 && javaIndex < dataTable.getColumnCount())) {
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

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(DataTable.class)) {
            return (T) dataTable;
        }

        return super.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(DataTable.class) || super.isWrapperFor(iface);
    }

    @Override
    public String getCursorName() throws SQLException {
        throw JDBCError.JDBC_FEATURE_NOT_SUPPORTED.raiseSQLException("Retrieval of cursor name");
    }
}
