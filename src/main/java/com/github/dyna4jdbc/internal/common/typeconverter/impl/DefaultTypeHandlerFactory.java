package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

public class DefaultTypeHandlerFactory implements TypeHandlerFactory {

	private final ColumnMetadataFactory columnMetadataFactory;

	private DefaultTypeHandlerFactory(Configuration configuration) {
		this.columnMetadataFactory = new DefaultColumnMetadataFactory(configuration);
	}

	public static DefaultTypeHandlerFactory getInstance(Configuration configuration) {
		return new DefaultTypeHandlerFactory(configuration);
	}

	@Override
	public TypeHandler newTypeHandler(int columnNumber, Iterable<String> columnIterable) {

		ColumnMetadata columnMetadata = columnMetadataFactory.getColumnMetadata(columnNumber, columnIterable);

		return new DefaultTypeHandler(columnMetadata);
	}
}
