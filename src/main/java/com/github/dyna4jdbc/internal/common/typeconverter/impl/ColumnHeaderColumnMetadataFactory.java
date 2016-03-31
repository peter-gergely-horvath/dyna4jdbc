
package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.util.collection.AlwaysSkipFirstElementIterable;
import com.github.dyna4jdbc.internal.config.Configuration;

final class ColumnHeaderColumnMetadataFactory extends HeuristicsColumnMetadataFactory implements ColumnMetadataFactory {

	private final Configuration configuration;
	private static final Pattern SQL_TYPE_PATTERN = Pattern.compile("\\s*(\\w+)(?:\\s*[(]\\s*(\\d+)\\s*[,]?\\s*(\\d)?\\s*[)])?\\s*");
	

	ColumnHeaderColumnMetadataFactory(Configuration configuration) {
		this.configuration = configuration;
	}

	static ColumnHeaderColumnMetadataFactory getInstance(Configuration configuration) {
		return new ColumnHeaderColumnMetadataFactory(configuration);
	}

	protected void configureForValues(DefaultColumnMetadata metaData,
			int columnIndex, Iterable<String> columnValuesIterable) {
		
		
		Iterator<String> iterator = columnValuesIterable.iterator();

		if (!iterator.hasNext()) {
			throw SQLError.raiseInternalIllegalStateRuntimeException("iterator is empty: could not extract header value");
		}
		
		String firstValue = iterator.next();
		String[] configStringArray = firstValue.split(":");
		String header = configStringArray[0];
		String sqlTypeConfig = configStringArray.length >= 2 ? configStringArray[1] : null;
		String metaDataConfig = configStringArray.length == 3 ? configStringArray[2] : null;
		
		super.configureForValues(metaData, columnIndex, AlwaysSkipFirstElementIterable.<String>newInstance(columnValuesIterable));
		
		
		if(header != null && !"".equals(header.trim())) {
			configureHeader(metaData, header);
		}
		if(sqlTypeConfig != null && !"".equals(sqlTypeConfig.trim())) {
			configureSqlType(metaData, sqlTypeConfig);
		}
		if(metaDataConfig != null && !"".equals(metaDataConfig.trim())) {
			configureAdditional(metaData, metaDataConfig);
		}
		
		metaData.setTakesFirstRowValue(true);

	}
	
	private void configureHeader(DefaultColumnMetadata metaData, String header) {
		metaData.setColumnLabel(header);
		metaData.setColumnName(header);
	}
	
	private void configureSqlType(DefaultColumnMetadata metaData, String sqlTypeConfig) {

		try {
			Matcher matcher = SQL_TYPE_PATTERN.matcher(sqlTypeConfig);
			if(! matcher.matches()) {
				throw SQLError.INVALID_CONFIGURATION_HEADER.raiseSQLException(sqlTypeConfig);
			}
			
			String sqlTypePart = matcher.group(1);
			String scalePart = matcher.group(2);
			String precisionPart = matcher.group(3);
			
			
			SQLDataType sqlDataType = SQLDataType.valueOf(sqlTypePart.toUpperCase());
			metaData.setColumnType(sqlDataType);
			
			if(scalePart != null) {
				metaData.setScale(Integer.valueOf(scalePart));
			}
				
			if(precisionPart != null) {
				metaData.setPrecision(Integer.valueOf(precisionPart));
			}
			
			
		} catch(Exception e) {
			throw new IllegalStateException("Processing of header failed: " + sqlTypeConfig + "; " + e.getMessage(), e);
		}

	}
	
	private void configureAdditional(DefaultColumnMetadata metaData, String metaDataConfig) {
		// no-op: TODO: implement as necessary
	}


}
