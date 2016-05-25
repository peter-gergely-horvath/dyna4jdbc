package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.datamodel.DataColumn;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.base.DataRowListResultSet;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

import java.sql.*;
import java.util.*;

public final class DataTableHolderResultSet extends DataRowListResultSet<List<String>> {

	private final DataTable dataTable;

	public DataTableHolderResultSet(
			Statement statement, DataTable dataTable, TypeHandlerFactory typeHandlerFactory) {

		super(dataTable.getRows(), statement, initTypeHandlers(dataTable, typeHandlerFactory));
		this.dataTable = dataTable;

		if (checkFirstRowIsSkipped(this.typeHandlers)) {
			super.skipNextRowIfPresent();
		}
	}

	private static List<TypeHandler> initTypeHandlers(DataTable dataTable, TypeHandlerFactory typeHandlerFactory) {

		LinkedList<TypeHandler> typeHandlerList = new LinkedList<>();

		int columnIndex = 0;

		for (DataColumn column : dataTable.columnIterable()) {
			TypeHandler typeHandler = typeHandlerFactory.newTypeHandler(columnIndex++, column);
			if (typeHandler == null) {
				throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("typeHandler is null");
			}

			typeHandlerList.add(typeHandler);
		}

		return Collections.unmodifiableList(typeHandlerList);
	}

	private static boolean checkFirstRowIsSkipped(List<TypeHandler> typeHandlers) {

		Boolean shouldTakeFirstRowValue = null;

		final int typeHandlerNumber = typeHandlers.size();
		for (int i = 0; i < typeHandlerNumber; i++) {

			TypeHandler typeHandler = typeHandlers.get(i);

			final boolean thisTypeHandlerTakesFirstRowValue = typeHandler.getColumnMetadata().isTakesFirstRowValue();

			if (shouldTakeFirstRowValue == null) {
				shouldTakeFirstRowValue = thisTypeHandlerTakesFirstRowValue;
			} else {
				if (shouldTakeFirstRowValue != thisTypeHandlerTakesFirstRowValue) {

					final int sqlIndexOfColumn = i + 1;

					JDBCError.INCONSISTENT_HEADER_SPECIFICATION.raiseUncheckedException(
							String.format("Column specification for column %s is invalid / inconsistent: " +
									"ensure SQL type is valid and that explicit declaration / " +
									"heuristic detection is not mixed. "
									+ "(HINT: Isn't there any typo or unintended column " +
									"separator in the header line?)", sqlIndexOfColumn));
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
			throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Invalid index: " + sqlColumnIndex);
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
	public final String getCursorName() throws SQLException {
		throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("Retrieval of cursor name");
	}
}
