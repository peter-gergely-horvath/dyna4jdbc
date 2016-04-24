package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.datamodel.DataColumn;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSet;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

public class DataTableHolderResultSet extends AbstractResultSet<List<String>> implements ResultSet {

	private final DataTable dataTable;

	private boolean wasNull = false;
	private List<TypeHandler> typeHandlers;
	private Map<String, Integer> columnNameToColumnIndexMap;

	public DataTableHolderResultSet(Statement statement, DataTable dataTable, TypeHandlerFactory typeHandlerFactory) {
		super(dataTable, statement);
		this.dataTable = dataTable;
		this.typeHandlers = initTypeHandlers(dataTable, typeHandlerFactory);
		if (checkFirstRowIsSkipped(this.typeHandlers)) {
			super.skipNextRowIfPresent();
		}
		this.columnNameToColumnIndexMap = initColumnNameToColumnIndexMap(this.typeHandlers);
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
							String.format("Column specification for column %s is invalid / inconsistent: ensure SQL type is valid "
									+ "and that explicit declaration / heuristic detection is not mixed. "
									+ "(HINT: Isn't there any typo or unintended column separator in the header line?)", sqlIndexOfColumn));
				}
			}
		}

		if (shouldTakeFirstRowValue == null) {
			shouldTakeFirstRowValue = false;
		}

		return shouldTakeFirstRowValue;
	}

	private static Map<String, Integer> initColumnNameToColumnIndexMap(List<TypeHandler> columnTypeHandlers) {

		HashMap<String, Integer> columnNameToColumnIndexMap = new HashMap<>();

		int sqlIndex = 1;

		for (TypeHandler typeHandler : columnTypeHandlers) {

			ColumnMetadata columnMetadata = typeHandler.getColumnMetadata();
			if (columnMetadata == null) {
				throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("columnMetadata is null");
			}

			String columnLabel = columnMetadata.getColumnLabel();
			if (columnNameToColumnIndexMap.containsKey(columnLabel)) {
				throw new IllegalStateException("Duplicate column label: " + columnLabel);
			}

			columnNameToColumnIndexMap.put(columnLabel, sqlIndex);

			sqlIndex++;
		}

		return Collections.unmodifiableMap(columnNameToColumnIndexMap);
	}

	@Override
	public void close() throws SQLException {
		
		/* "Calling the method close on a ResultSet 
		 * object that is already closed is a no-op." */
		if(! isClosed()) {
			dataTable.clear();

			super.close();
		}
	}

	private String getRawCellValueBySqlIndex(int sqlIndex) throws SQLException {

		final int javaIndex = sqlIndex - 1;

		List<String> currentRow = getCurrentRow();

		if (!(javaIndex >= 0 && javaIndex < dataTable.getColumnCount())) {
			throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Invalid index: " + sqlIndex);
		}

		String cellValue;
		if (javaIndex < currentRow.size()) {
			cellValue = currentRow.get(javaIndex);
		} else {
			cellValue = null;
		}

		return cellValue;
	}

	private TypeHandler getTypeHandlerByBySqlIndex(int sqlIndex) throws SQLException {
		final int javaIndex = sqlIndex - 1;

		TypeHandler typeHandler = typeHandlers.get(javaIndex);

		return typeHandler;
	}

	private <T> T setWasNullBasedOnLastValue(T convertedValue) {
		wasNull = (convertedValue == null);
		return convertedValue;
	}

	@Override
	public boolean wasNull() throws SQLException {
		return wasNull;
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			String convertedValue = typeHandler.covertToString(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.lang.String.class);
		}
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Boolean convertedValue = typeHandler.covertToBoolean(rawCellValue);
			Boolean returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.booleanValue() : false;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(
					tce, getRow(), columnIndex, rawCellValue, "boolean");
		}
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {

		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Byte convertedValue = typeHandler.covertToByte(rawCellValue);

			Byte returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.byteValue() : 0x0;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue, "byte");
		}
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Short convertedValue = typeHandler.covertToShort(rawCellValue);
			Short returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.shortValue() : 0;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue, "short");
		}
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Integer convertedValue = typeHandler.covertToInteger(rawCellValue);

			Integer returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.intValue() : 0;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue, "int");
		}
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Long convertedValue = typeHandler.covertToLong(rawCellValue);

			Long returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.longValue() : 0L;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue, "long");
		}
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Float convertedValue = typeHandler.covertToFloat(rawCellValue);

			Float returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.floatValue() : 0.0f;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue, "float");
		}
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Double convertedValue = typeHandler.covertToDouble(rawCellValue);

			Double returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue.doubleValue() : 0.0d;

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					"double");
		}
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			BigDecimal convertedValue = typeHandler.covertToBigDecimal(rawCellValue, scale);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.math.BigDecimal.class);
		}
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			byte[] convertedValue = typeHandler.covertToByteArray(rawCellValue);

			byte[] returnValue = setWasNullBasedOnLastValue(convertedValue);

			return returnValue != null ? returnValue : new byte[0];

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					"byte[]");
		}
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Date convertedValue = typeHandler.covertToDate(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.sql.Date.class);
		}
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Time convertedValue = typeHandler.covertToTime(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.sql.Time.class);
		}
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Timestamp convertedValue = typeHandler.covertToTimestamp(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.sql.Timestamp.class);
		}
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			InputStream convertedValue = typeHandler.covertToAsciiInputStream(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					"(ASCII) InputStream");
		}
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			InputStream convertedValue = typeHandler.covertToUnicodeInputStream(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					"(Unicode) InputStream");
		}
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			InputStream convertedValue = typeHandler.covertToBinaryInputStream(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					"(Binary) InputStream");
		}
	}

	@Override
	public String getString(String columnLabel) throws SQLException {
		return getString(findColumn(columnLabel));
	}

	@Override
	public boolean getBoolean(String columnLabel) throws SQLException {
		return getBoolean(findColumn(columnLabel));
	}

	@Override
	public byte getByte(String columnLabel) throws SQLException {
		return getByte(findColumn(columnLabel));
	}

	@Override
	public short getShort(String columnLabel) throws SQLException {
		return getShort(findColumn(columnLabel));
	}

	@Override
	public int getInt(String columnLabel) throws SQLException {
		return getInt(findColumn(columnLabel));
	}

	@Override
	public long getLong(String columnLabel) throws SQLException {
		return getLong(findColumn(columnLabel));
	}

	@Override
	public float getFloat(String columnLabel) throws SQLException {
		return getFloat(findColumn(columnLabel));
	}

	@Override
	public double getDouble(String columnLabel) throws SQLException {
		return getDouble(findColumn(columnLabel));
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
		return getBigDecimal(findColumn(columnLabel), scale);
	}

	@Override
	public byte[] getBytes(String columnLabel) throws SQLException {
		return getBytes(findColumn(columnLabel));
	}

	@Override
	public Date getDate(String columnLabel) throws SQLException {
		return getDate(findColumn(columnLabel));
	}

	@Override
	public Time getTime(String columnLabel) throws SQLException {
		return getTime(findColumn(columnLabel));
	}

	@Override
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return getTimestamp(findColumn(columnLabel));
	}

	@Override
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return getAsciiStream(findColumn(columnLabel));
	}

	@Override
	public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		return getUnicodeStream(findColumn(columnLabel));
	}

	@Override
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return getBinaryStream(findColumn(columnLabel));
	}

	@Override
	public String getCursorName() throws SQLException {
		throw JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.raiseSQLException("Retrieval of cursor name");
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return new DataTableHolderResultSetMetaData(typeHandlers);
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		return getObject(columnIndex, (Map<String, Class<?>>) null);

	}

	@Override
	public Object getObject(String columnLabel) throws SQLException {
		return getObject(findColumn(columnLabel));
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		if (!columnNameToColumnIndexMap.containsKey(columnLabel)) {
			throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Invalid column label: " + columnLabel);
		}

		Integer sqlIndex = columnNameToColumnIndexMap.get(columnLabel);
		if (sqlIndex == null) {
			throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException("sqlIndex is null");
		}

		return sqlIndex;
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Reader convertedValue = typeHandler.covertToCharacterStream(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.io.Reader.class);
		}
	}

	@Override
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return getCharacterStream(findColumn(columnLabel));
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			BigDecimal convertedValue = typeHandler.covertToBigDecimal(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.io.Reader.class);
		}
	}

	@Override
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return getBigDecimal(findColumn(columnLabel));
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Object convertedValue = typeHandler.covertToObject(rawCellValue, map);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.lang.Object.class);
		}
	}

	@Override
	public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
		return getObject(findColumn(columnLabel), map);
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Date convertedValue = typeHandler.covertToDate(rawCellValue, cal);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.sql.Date.class);
		}
	}

	@Override
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return getDate(findColumn(columnLabel), cal);
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Time convertedValue = typeHandler.covertToTime(rawCellValue, cal);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.sql.Time.class);
		}
	}

	@Override
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return getTime(findColumn(columnLabel), cal);
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			Timestamp convertedValue = typeHandler.covertToTimestamp(rawCellValue, cal);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.sql.Timestamp.class);
		}
	}

	@Override
	public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
		return getTimestamp(findColumn(columnLabel), cal);
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			URL convertedValue = typeHandler.covertToURL(rawCellValue);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue,
					java.net.URL.class);
		}
	}

	@Override
	public URL getURL(String columnLabel) throws SQLException {
		return getURL(findColumn(columnLabel));
	}

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

		try {
			TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
			T convertedValue = typeHandler.covertToObject(rawCellValue, type);
			return setWasNullBasedOnLastValue(convertedValue);

		} catch (TypeConversionException tce) {
			throw JDBCError.DATA_CONVERSION_FAILED.raiseSQLException(tce, getRow(), columnIndex, rawCellValue, type);
		}
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return getObject(findColumn(columnLabel), type);
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
}
