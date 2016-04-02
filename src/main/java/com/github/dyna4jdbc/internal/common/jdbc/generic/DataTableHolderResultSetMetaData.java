package com.github.dyna4jdbc.internal.common.jdbc.generic;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractResultSetMetaData;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.SQLDataType;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class DataTableHolderResultSetMetaData extends AbstractResultSetMetaData {

	private final List<TypeHandler> typeHandlerList;

	public DataTableHolderResultSetMetaData(List<TypeHandler> typeHandlerList) {
		this.typeHandlerList = typeHandlerList;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return typeHandlerList.size();
	}

	private ColumnMetadata getColumnMetadataBySqlIndex(int sqlColumnIndex) throws SQLException {
		final int javaIndex = sqlColumnIndex - 1;

		if (javaIndex < 0 || javaIndex >= getColumnCount()) {
			throw JDBCError.JDBC_API_USAGE_CALLER_ERROR.raiseSQLException("Invalid column index: " + sqlColumnIndex);
		}

		TypeHandler typeHandler = typeHandlerList.get(javaIndex);

		ColumnMetadata columnMetadata = typeHandler.getColumnMetadata();
		if (columnMetadata == null) {
			throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException("columnMetadata is null");
		}

		return columnMetadata;
	}

	@Override
	public boolean isCurrency(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).isCurrency();
	}

	@Override
	public int isNullable(int column) throws SQLException {

		// adapt our non-JDBC specific Nullability to JDBC API values 
		Nullability nullability = getColumnMetadataBySqlIndex(column).getNullability();
		if(nullability == null) {
			throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
					"Column Nullability indicator is null.");
		}

		switch (nullability) {
		case NOT_NULLABLE:
			return ResultSetMetaData.columnNoNulls;

		case NULLABLE:
			return ResultSetMetaData.columnNullable;

		case UNKNOWN:
			return ResultSetMetaData.columnNullableUnknown;

		default:
			throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
					"Unknown Nullability value: " + nullability);
		}
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).isSigned();
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).getColumnDisplaySize();
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).getColumnLabel();
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).getColumnName();
	}

	@Override
	public int getPrecision(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).getPrecision();
	}

	@Override
	public int getScale(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).getScale();
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		return getColumnMetadataBySqlIndex(column).getColumnType().javaSqlTypesInt;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		ColumnMetadata columnMetadata = getColumnMetadataBySqlIndex(column);
		
		String columnTypeName = columnMetadata.getColumnTypeName();
		
		
		if(columnTypeName == null) {
			SQLDataType columnType = getColumnMetadataBySqlIndex(column).getColumnType();
			if(columnType == null) {
				throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
						"columnType is null");
			}
			
			columnTypeName = columnType.name;
		}
		
		return columnTypeName;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		ColumnMetadata columnMetadata = getColumnMetadataBySqlIndex(column);
		
		
		Class<?> columnClass = columnMetadata.getColumnClass();
		
		if(columnClass == null) {
			SQLDataType columnType = getColumnMetadataBySqlIndex(column).getColumnType();
			if(columnType == null) {
				throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseSQLException(
						"columnType is null");
			}
			
			if(columnType.mappingClass != null) {
				columnClass = columnType.mappingClass;
			}
		}
		
		return columnClass != null ? columnClass.getName() : null;
	}
}
