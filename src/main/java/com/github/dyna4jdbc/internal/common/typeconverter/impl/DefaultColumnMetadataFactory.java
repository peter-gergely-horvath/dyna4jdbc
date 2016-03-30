package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.util.Iterator;
import java.util.regex.Pattern;

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

		final String headerValue = iterator.next();

		final boolean headerSeemsToContainParseInstructions = 
				headerValue != null && HEADER_PATTERN.matcher(headerValue).matches();

		if (headerSeemsToContainParseInstructions) {
			return ColumnHeaderColumnMetadataFactory.getInstance().getColumnMetadataFromHeader(columnIndex, headerValue);
		} else {
			return HeuristicsColumnMetadataFactory.getInstance().getColumnMetadataFromColumnValues(columnIndex, columnValuesIterable);
		}

	}
	

	
	

}
