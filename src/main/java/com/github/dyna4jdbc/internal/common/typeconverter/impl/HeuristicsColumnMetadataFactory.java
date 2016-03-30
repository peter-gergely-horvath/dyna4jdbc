package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnMetaData.Builder;
import com.github.dyna4jdbc.internal.config.Configuration;

class HeuristicsColumnMetadataFactory implements ColumnMetadataFactory {

	private static final HeuristicsColumnMetadataFactory INSTANCE = new HeuristicsColumnMetadataFactory();

	static HeuristicsColumnMetadataFactory getInstance(Configuration configuration) {
		return INSTANCE;
	}
	

	@Override
	public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValuesIterable) {
		Builder columnMetaDataBuilder = DefaultColumnMetaData.builder();
		configureForValues(columnMetaDataBuilder, columnIndex, columnValuesIterable);
		return columnMetaDataBuilder.build();
	}

	protected DefaultColumnMetaData.Builder configureForValues(DefaultColumnMetaData.Builder metaData,
			int columnIndex, Iterable<String> columnValuesIterable) {

		final int sqlColumnIndex = columnIndex + 1; 
		
		int maxSize = 0;
		int maxPrecision = 0;
		int maxScale = 0;

		Nullability nullability = Nullability.NOT_NULLABLE;

		SQLDataType columnType = SQLDataType.OTHER;

		for (String cellValue : columnValuesIterable) {

			columnType = getColumnTypeByCurrentlySelectedTypeAndCellValue(columnType, cellValue);

			if (cellValue == null) {
				nullability = Nullability.NULLABLE;
			} else {
				maxSize = Math.max(maxSize, cellValue.length());
				maxPrecision = Math.max(maxPrecision, columnType.getPrecision(cellValue));
				maxScale = Math.max(maxScale, columnType.getScale(cellValue));
			}
		}

		final int columnDisplaySize = maxSize >= 15 ? maxSize : 15;
		
		metaData.setTakesFirstRowValue(false);
		metaData.setCurrency(false);
		metaData.setNullability(nullability);
		metaData.setSigned(columnType != SQLDataType.VARCHAR);
		metaData.setColumnDisplaySize(columnDisplaySize);
		metaData.setColumnLabel(String.valueOf(sqlColumnIndex));
		metaData.setColumnName(String.valueOf(sqlColumnIndex));
		metaData.setPrecision(maxPrecision);
		metaData.setScale(maxScale);
		metaData.setColumnType(columnType);

		return metaData;
	}

	private static SQLDataType getColumnTypeByCurrentlySelectedTypeAndCellValue(SQLDataType currentColumnType, String cellValue) {
		
		SQLDataType newColumnType = currentColumnType;
		
		switch (currentColumnType) {
		case OTHER:
			if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)) {
				newColumnType = SQLDataType.DOUBLE;
				break;
			}
			if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
				newColumnType = SQLDataType.INTEGER;
				break;
			}
			if (SQLDataType.VARCHAR.isPlausibleConversion(cellValue)) {
				newColumnType = SQLDataType.VARCHAR;
				break;
			}

			break;

		case INTEGER:
			if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
				newColumnType = SQLDataType.INTEGER;
				break;
			}

		case DOUBLE:
			if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)
					|| SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
				newColumnType = SQLDataType.DOUBLE;
				break;
			}

		case VARCHAR:
			newColumnType = SQLDataType.VARCHAR;
			break;

		default:
			throw SQLError.raiseInternalIllegalStateRuntimeException("Unexpected columnType: " + newColumnType);
		}
		return newColumnType;
	}


}
