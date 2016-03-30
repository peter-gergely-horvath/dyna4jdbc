
package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnMetaData.Builder;

final class ColumnHeaderColumnMetadataFactory {

	private static final ColumnHeaderColumnMetadataFactory INSTANCE = new ColumnHeaderColumnMetadataFactory();

	static ColumnHeaderColumnMetadataFactory getInstance() {
		return INSTANCE;
	}
	
	ColumnMetadata getColumnMetadataFromHeader(int columnIndex, String headerValue) {
		
		Builder columnMetaDataBuilder = DefaultColumnMetaData.builder();
		configureForHeader(columnMetaDataBuilder, columnIndex,  headerValue);
		return columnMetaDataBuilder.build();
	}
	
	private static DefaultColumnMetaData.Builder configureForHeader(DefaultColumnMetaData.Builder metaData, int columnIndex, String headerValue) {
		
		return metaData;
	}

}
