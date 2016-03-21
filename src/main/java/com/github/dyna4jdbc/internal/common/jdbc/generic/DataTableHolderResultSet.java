package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.datamodel.DataCell;
import com.github.dyna4jdbc.internal.common.datamodel.DataColumn;
import com.github.dyna4jdbc.internal.common.datamodel.DataRow;
import com.github.dyna4jdbc.internal.common.datamodel.DataTable;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSet;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

public class DataTableHolderResultSet extends AbstractResultSet<DataRow> implements ResultSet {

    private final DataTable dataTable;

    private boolean wasNull = false;
	private List<TypeHandler> typeHandlers;

    public DataTableHolderResultSet(Statement statement, DataTable dataTable, TypeHandlerFactory typeHandlerFactory) {
        super(dataTable.rowIterator(), statement);
        this.dataTable = dataTable;
        this.typeHandlers = initTypeHandlers(dataTable, typeHandlerFactory);
    }

    private static List<TypeHandler> initTypeHandlers(DataTable dataTable, TypeHandlerFactory typeHandlerFactory) {

    	LinkedList<TypeHandler> typeHandlerList = new LinkedList<>();
    	
    	for(DataColumn column : dataTable.columnIterable() ) {
    		TypeHandler typeHandler = typeHandlerFactory.newTypeHandler(column.valueIterable());
    		if (typeHandler == null) {
    			throw SQLError.raiseInternalIllegalStateRuntimeException("typeHandler is null");
    		}
    		
    		typeHandlerList.add(typeHandler);
    	}
    	
    	return Collections.unmodifiableList(typeHandlerList);
    }

    @Override
    public void close() throws SQLException {
        checkNotClosed();

        dataTable.clear();

        super.close();
    }

    private String getRawCellValueBySqlIndex(int sqlIndex) throws SQLException {

        final int javaIndex = sqlIndex - 1;


        DataRow currentRow = getCurrentRow();
        
        if (!currentRow.isValidIndex(javaIndex)) {
            throw SQLError.JDBC_API_USAGE_CALLER_ERROR.raiseException(
                    "Invalid index: " + sqlIndex);
        }

        DataCell dataCell = currentRow.getCell(javaIndex);
        if (dataCell == null) {
            throw SQLError.DRIVER_BUG_UNEXPECTED_STATE.raiseException(
                    "Indexed dataCell not found: " + javaIndex);
        }

        String cellValue = dataCell.getValue();
        wasNull = cellValue == null;

        return cellValue;
    }
    
    private TypeHandler getTypeHandlerByBySqlIndex(int sqlIndex) throws SQLException {
        final int javaIndex = sqlIndex - 1;

        TypeHandler typeHandler = typeHandlers.get(javaIndex);
        
		return typeHandler;
    }
    
    @SuppressWarnings("unchecked")
	private <T> T checkIfNull(Object formattedValue) {
    	wasNull = (formattedValue == null);
    	return (T) formattedValue;
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
    		String formattedValue = typeHandler.covertToString(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, java.lang.String.class);
    	}
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Boolean formattedValue = typeHandler.covertToBoolean(rawCellValue);
    		Boolean returnValue = checkIfNull(formattedValue);

    		return returnValue != null ? returnValue.booleanValue() : false; 

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "boolean");
    	}
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {

    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Byte formattedValue = typeHandler.covertToByte(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "byte");
    	}
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Short formattedValue = typeHandler.covertToShort(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "short");
    	}
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Integer formattedValue = typeHandler.covertToInteger(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "int");
    	}
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Long formattedValue = typeHandler.covertToLong(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "long");
    	}
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Float formattedValue = typeHandler.covertToFloat(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "float");
    	}
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Double formattedValue = typeHandler.covertToDouble(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "double");
    	}
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		BigDecimal formattedValue = typeHandler.covertToBigDecimal(rawCellValue, scale);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, java.math.BigDecimal.class);
    	}
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		byte[] formattedValue = typeHandler.covertToByteArray(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, "byte[]");
    	}
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Date formattedValue = typeHandler.covertToDate(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, java.sql.Date.class);
    	}
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Time formattedValue = typeHandler.covertToTime(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, java.sql.Time.class);
    	}
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
    	String rawCellValue = getRawCellValueBySqlIndex(columnIndex);

    	try {
    		TypeHandler typeHandler = getTypeHandlerByBySqlIndex(columnIndex);
    		Timestamp formattedValue = typeHandler.covertToTimestamp(rawCellValue);
    		return checkIfNull(formattedValue);

    	} catch(TypeConversionException tce) {
    		throw SQLError.DATA_CONVERSION_FAILED.raiseException(
    				tce, rawCellValue, java.sql.Timestamp.class);
    	}
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getAsciiStream"); // TODO: implement method
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getUnicodeStream"); // TODO: implement method
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBinaryStream"); // TODO: implement method
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getString"); // TODO: implement method
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBoolean"); // TODO: implement method
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getByte"); // TODO: implement method
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getShort"); // TODO: implement method
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getInt"); // TODO: implement method
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getLong"); // TODO: implement method
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getFloat"); // TODO: implement method
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDouble"); // TODO: implement method
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBigDecimal"); // TODO: implement method
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBytes"); // TODO: implement method
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getAsciiStream"); // TODO: implement method
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getUnicodeStream"); // TODO: implement method
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBinaryStream"); // TODO: implement method
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public String getCursorName() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getCursorName"); // TODO: implement method
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
		return new DataTableHolderResultSetMetaData(typeHandlers);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#findColumn"); // TODO: implement method
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getCharacterStream"); // TODO: implement method
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getCharacterStream"); // TODO: implement method
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBigDecimal"); // TODO: implement method
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBigDecimal"); // TODO: implement method
    }





    @Override
    public boolean absolute(int row) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#absolute"); // TODO: implement method
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#relative"); // TODO: implement method
    }

    @Override
    public boolean previous() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#previous"); // TODO: implement method
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#setFetchDirection"); // TODO: implement method
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getFetchDirection"); // TODO: implement method
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#setFetchSize"); // TODO: implement method
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getFetchSize"); // TODO: implement method
    }

    @Override
    public int getType() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getType"); // TODO: implement method
    }

    @Override
    public int getConcurrency() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getConcurrency"); // TODO: implement method
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#rowUpdated"); // TODO: implement method
    }

    @Override
    public boolean rowInserted() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#rowInserted"); // TODO: implement method
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#rowDeleted"); // TODO: implement method
    }


    @Override
    public void insertRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#insertRow"); // TODO: implement method
    }

    @Override
    public void updateRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRow"); // TODO: implement method
    }

    @Override
    public void deleteRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#deleteRow"); // TODO: implement method
    }

    @Override
    public void refreshRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#refreshRow"); // TODO: implement method
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#cancelRowUpdates"); // TODO: implement method
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#moveToInsertRow"); // TODO: implement method
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#moveToCurrentRow"); // TODO: implement method
    }


    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRef"); // TODO: implement method
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBlob"); // TODO: implement method
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getClob"); // TODO: implement method
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getArray"); // TODO: implement method
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRef"); // TODO: implement method
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getBlob"); // TODO: implement method
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getClob"); // TODO: implement method
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getArray"); // TODO: implement method
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getDate"); // TODO: implement method
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTime"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getTimestamp"); // TODO: implement method
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getURL"); // TODO: implement method
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getURL"); // TODO: implement method
    }

   

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRowId"); // TODO: implement method
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getRowId"); // TODO: implement method
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRowId"); // TODO: implement method
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateRowId"); // TODO: implement method
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getHoldability"); // TODO: implement method
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNString"); // TODO: implement method
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNString"); // TODO: implement method
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNClob"); // TODO: implement method
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateNClob"); // TODO: implement method
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNClob"); // TODO: implement method
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNClob"); // TODO: implement method
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getSQLXML"); // TODO: implement method
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getSQLXML"); // TODO: implement method
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateSQLXML"); // TODO: implement method
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#updateSQLXML"); // TODO: implement method
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNString"); // TODO: implement method
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNString"); // TODO: implement method
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNCharacterStream"); // TODO: implement method
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getNCharacterStream"); // TODO: implement method
    }


    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new UnsupportedOperationException("com.github.dyna4jdbc.internal.common.jdbc.generic.DataTableHolderResultSet#getObject"); // TODO: implement method
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if(iface.isAssignableFrom(DataTable.class)) {
        	return (T) dataTable;
        }
        
        throw SQLError.CANNOT_UNWARP_OBJECT.raiseException(iface, this.getClass());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(DataTable.class);
    }
}
