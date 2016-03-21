package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.Iterator;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;

public class DefaultTypeHandlerFactory implements TypeHandlerFactory {

	@Override
	public TypeHandler newTypeHandler(Iterable<String> columnIterable) {

		Iterator<String> iterator = columnIterable.iterator();

		String header = "";
		if (iterator.hasNext()) {
			header = iterator.next();
		}

		return new DefaultTypeHandler(new DummyColumnMetadata(header));
	}

	final static class DummyColumnMetadata implements ColumnMetadata {

		private final String header;

		private DummyColumnMetadata(String header) {
			this.header = header;
		}

		@Override
		public boolean isCurrency() {
			return false;
		}

		@Override
		public int isNullable() {
			return ResultSetMetaData.columnNullable; // TODO: decouple
														// this!!
		}

		@Override
		public boolean isSigned() {
			return false;
		}

		@Override
		public int getColumnDisplaySize() {
			return header.length();
		}

		@Override
		public String getColumnLabel() {
			return header;
		}

		@Override
		public String getColumnName() {
			return header;
		}

		@Override
		public int getPrecision() {
			return 0;
		}

		@Override
		public int getScale() {
			return 0;
		}

		@Override
		public int getColumnType() {
			return Types.VARCHAR;
		}

		@Override
		public String getColumnTypeName() {
			return "VARCHAR";
		}

		@Override
		public Class<?> getColumnClass() {
			return java.lang.String.class;
		}
	}
}
