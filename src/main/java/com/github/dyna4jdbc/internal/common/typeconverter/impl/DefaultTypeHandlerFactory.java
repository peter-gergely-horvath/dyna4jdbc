package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

public class DefaultTypeHandlerFactory implements TypeHandlerFactory {

	
	private static final DefaultTypeHandlerFactory INSTANCE = new DefaultTypeHandlerFactory();
	
	public static DefaultTypeHandlerFactory getInstance() {
		return INSTANCE;
	}
	
	private ColumnMetadataFactory columnMetadataFactory = new DefaultColumnMetadataFactory();
	
	
	@Override
	public TypeHandler newTypeHandler(int columnNumber, Iterable<String> columnIterable) {

		ColumnMetadata columnMetadata = columnMetadataFactory.getColumnMetadata(columnNumber, columnIterable);

		return new DefaultTypeHandler(columnMetadata);
	}
}
