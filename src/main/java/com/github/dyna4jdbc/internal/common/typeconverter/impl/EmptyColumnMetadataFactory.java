package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.config.Configuration;

public class EmptyColumnMetadataFactory implements ColumnMetadataFactory {

	private static final EmptyColumnMetadataFactory INSTANCE = new EmptyColumnMetadataFactory();

	static EmptyColumnMetadataFactory getInstance(Configuration configuration) {
		return INSTANCE;
	}

	private EmptyColumnMetadataFactory() {
	}

	private final ColumnMetadata EMPTY_COLUMN_METADATA = DefaultColumnMetaData.builder()
			.setTakesFirstRowValue(false)
			.setCurrency(false)
			.setNullability(Nullability.UNKNOWN)
			.setSigned(false)
			.setColumnDisplaySize(4)
			.setColumnLabel("")
			.setColumnName("")
			.setPrecision(0)
			.setScale(0)
			.setColumnType(SQLDataType.VARCHAR)
			.build();
	

	@Override
	public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValuesIterable) {
		return EMPTY_COLUMN_METADATA;
	}
	
}
