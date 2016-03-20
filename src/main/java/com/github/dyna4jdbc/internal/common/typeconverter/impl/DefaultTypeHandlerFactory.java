package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
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

		return new DummyStringTypeHandler(header);
	}

	private static class DummyStringTypeHandler extends AbstractTypeHandler {

		DummyStringTypeHandler(String header) {
			super(new DummyColumnMetadata(header));
		}

		@Override
		public String covertToString(String rawCellValue) throws TypeConversionException {
			return rawCellValue;
		}

		@Override
		public Boolean covertToBoolean(String rawCellValue) throws TypeConversionException {

			if (rawCellValue == null) {
				return null;
			}

			return Boolean.valueOf(rawCellValue);
		}

		@Override
		public Byte covertToByte(String rawCellValue) throws TypeConversionException {

			try {
				if (rawCellValue == null) {
					return null;
				}

				return Byte.decode(rawCellValue);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public Short covertToShort(String rawCellValue) throws TypeConversionException {

			try {
				if (rawCellValue == null) {
					return null;
				}

				return Short.decode(rawCellValue);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public Integer covertToInteger(String rawCellValue) throws TypeConversionException {
			try {
				if (rawCellValue == null) {
					return null;
				}

				return Integer.decode(rawCellValue);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public Long covertToLong(String rawCellValue) throws TypeConversionException {
			try {
				if (rawCellValue == null) {
					return null;
				}

				return Long.decode(rawCellValue);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public Float covertToFloat(String rawCellValue) throws TypeConversionException {
			try {
				if (rawCellValue == null) {
					return null;
				}

				return Float.valueOf(rawCellValue);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public Double covertToDouble(String rawCellValue) throws TypeConversionException {
			try {
				if (rawCellValue == null) {
					return null;
				}

				return Double.valueOf(rawCellValue);

			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public BigDecimal covertToBigDecimal(String rawCellValue, int scale) throws TypeConversionException {
			try {
				if (rawCellValue == null) {
					return null;
				}

				return BigDecimal.valueOf(covertToLong(rawCellValue), scale);
			} catch (NumberFormatException nfe) {
				throw new TypeConversionException(nfe);
			}
		}

		@Override
		public byte[] covertToByteArray(String rawCellValue) throws TypeConversionException {

			if (rawCellValue == null) {
				return null;
			}

			return rawCellValue.getBytes();
		}

		@Override
		public Date covertToDate(String rawCellValue) throws TypeConversionException {

			try {
				if (rawCellValue == null) {
					return null;
				}

				return Date.valueOf(rawCellValue);
			} catch (IllegalArgumentException e) {
				throw new TypeConversionException(e);
			}
		}

		@Override
		public Time covertToTime(String rawCellValue) throws TypeConversionException {

			try {
				if (rawCellValue == null) {
					return null;
				}

				return Time.valueOf(rawCellValue);
			} catch (IllegalArgumentException e) {
				throw new TypeConversionException(e);
			}
		}

		@Override
		public Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException {
			try {
				if (rawCellValue == null) {
					return null;
				}

				return Timestamp.valueOf(rawCellValue);
			} catch (IllegalArgumentException e) {
				throw new TypeConversionException(e);
			}
		}

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
