package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.util.Iterator;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;

public class DefaultColumnMetadataFactory implements ColumnMetadataFactory {

	// "Pattern: Instances of this class are immutable and are safe for use by
	// multiple concurrent threads."
	private static final Pattern HEADER_PATTERN = Pattern.compile("^[^:]+:[^:]*:[^:]*$");
	
	
	private ColumnMetadata EMPTY_COLUMN_METADATA = DefaultColumnMetaData.builder().setTakesFirstRowValue(false)
			.setCurrency(false).setNullability(Nullability.UNKNOWN).setSigned(false).setColumnDisplaySize(4)
			.setColumnLabel("").setColumnName("").setPrecision(0).setScale(0).setColumnType(SQLDataType.VARCHAR)
			.build();

	@Override
	public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValuesIterable) {

		Iterator<String> iterator = columnValuesIterable.iterator();

		if (!iterator.hasNext()) {
			return EMPTY_COLUMN_METADATA;
		}

		String headerValue = iterator.next();

		final boolean headerSeemsToContainParseInstructions = HEADER_PATTERN.matcher(headerValue).matches();

		if (headerSeemsToContainParseInstructions) {
			return configureForHeader(DefaultColumnMetaData.builder(), columnIndex,  headerValue).build();
		} else {
			return configureForValues(DefaultColumnMetaData.builder(), columnIndex, columnValuesIterable).build();
		}

	}
	
	private static DefaultColumnMetaData.Builder configureForHeader(DefaultColumnMetaData.Builder metaData, int columnIndex, String headerValue) {
		
		return metaData;
	}
	
	private static DefaultColumnMetaData.Builder configureForValues(DefaultColumnMetaData.Builder metaData, int columnIndex, Iterable<String> columnValuesIterable) {
		
		
		int maxSize = 0;
		Nullability nullability = Nullability.NOT_NULLABLE;
		
		SQLDataType columnType = SQLDataType.OTHER;
		
		for(String columnValue : columnValuesIterable) {
			
			if(columnValue == null) {
				nullability = Nullability.NULLABLE;
			} else {
				maxSize = Math.max(maxSize, columnValue.length());
			}
			
			switch (columnType) {
			case OTHER:
				if(SQLDataType.DOUBLE.isPlausibleConversion(columnValue)) {
					columnType = SQLDataType.DOUBLE;
					break;
				}
				if(SQLDataType.INTEGER.isPlausibleConversion(columnValue)) {
					columnType = SQLDataType.INTEGER;
					break;
				}
				if(SQLDataType.VARCHAR.isPlausibleConversion(columnValue)) {
					columnType = SQLDataType.VARCHAR;
					break;
				}
				
				break;

			case INTEGER:
				if(SQLDataType.INTEGER.isPlausibleConversion(columnValue)) {
					columnType = SQLDataType.INTEGER;
					break;
				}

			case DOUBLE:
				if(SQLDataType.DOUBLE.isPlausibleConversion(columnValue) ||
						SQLDataType.INTEGER.isPlausibleConversion(columnValue)) {
					columnType = SQLDataType.DOUBLE;
					break;
				}

			case VARCHAR:
				columnType = SQLDataType.VARCHAR;
				break;	
				
			default:
				throw SQLError.raiseInternalIllegalStateRuntimeException(
						"Unexpected columnType: " + columnType);
			}
		}
		
		maxSize = maxSize >= 15 ? maxSize : 15;
		
		return metaData
			.setTakesFirstRowValue(false)
			.setCurrency(false)
			.setNullability(nullability)
			.setSigned(columnType != SQLDataType.VARCHAR)
			.setColumnDisplaySize(maxSize)
			.setColumnLabel(String.valueOf(columnIndex))
			.setColumnName(String.valueOf(columnIndex))
			.setPrecision(0)
			.setScale(0)
			.setColumnType(columnType);
	}

}
