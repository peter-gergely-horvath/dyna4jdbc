package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConverter;

class DefaultTypeHandler extends AbstractTypeHandler {
	
	DefaultTypeHandler(ColumnMetadata columnMetadata) {
		super(columnMetadata);
	}
	
	@Override
	public String covertToString(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.STRING.convert(rawCellValue);
	}

	@Override
	public Boolean covertToBoolean(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.BOOLEAN.convert(rawCellValue);
	}

	@Override
	public Byte covertToByte(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.BYTE.convert(rawCellValue);
	}

	@Override
	public Short covertToShort(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.SHORT.convert(rawCellValue);
	}

	@Override
	public Integer covertToInteger(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.INTEGER.convert(rawCellValue);
	}


	@Override
	public Long covertToLong(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.LONG.convert(rawCellValue);
	}

	@Override
	public Float covertToFloat(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.FLOAT.convert(rawCellValue);
	}

	@Override
	public Double covertToDouble(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.DOUBLE.convert(rawCellValue);
	}

	@Override
	public BigDecimal covertToBigDecimal(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.BIGDECIMAL.convert(rawCellValue);
	}
	
	@Override
	public BigDecimal covertToBigDecimal(String rawCellValue, int scale) throws TypeConversionException {
		try {
			if (rawCellValue == null) {
				return null;
			}

			BigDecimal bigDecimal = covertToBigDecimal(rawCellValue);
			bigDecimal = bigDecimal.setScale(scale);
			
			return bigDecimal;
		} catch (ArithmeticException nfe) {
			throw new TypeConversionException(nfe);
		}
	}

	@Override
	public byte[] covertToByteArray(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.BYTE_ARRAY.convert(rawCellValue);
	}

	@Override
	public Date covertToDate(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.DATE.convert(rawCellValue);
	}

	@Override
	public Time covertToTime(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.TIME.convert(rawCellValue);
	}

	@Override
	public Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException {
		return TypeConverterRegistry.TIMESTAMP.convert(rawCellValue);
	}

	@Override
	public Object covertToObject(String rawCellValue, Map<String, Class<?>> map) throws TypeConversionException {
		
		SQLDataType columnType = columnMetadata.getColumnType();
		if(columnType == null) {
			throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("columnType is null");
		}
		
		Class<?> targetClass = map.get(columnType.name);
		if(targetClass == null) {
			throw TypeConversionException.forMessage(
					"Mapping to %s is not supported.", targetClass);
		}
		
		return covertToObject(rawCellValue, targetClass);
	}

	@Override
	public InputStream covertToAsciiInputStream(String rawCellValue) throws TypeConversionException {
		return covertToBinaryInputStream(rawCellValue);
	}

	@Override
	public InputStream covertToUnicodeInputStream(String rawCellValue) throws TypeConversionException {
		return covertToBinaryInputStream(rawCellValue);
	}

	@Override
	public InputStream covertToBinaryInputStream(String rawCellValue) throws TypeConversionException {
    	
    	byte[] bytes = covertToByteArray(rawCellValue);
        if(bytes != null) {
        	return new ByteArrayInputStream(bytes);
        }
        
        return null;
	}

	@Override
	public <T> T covertToObject(String rawCellValue, Class<T> type) throws TypeConversionException {
		
		TypeConverter<T> typeConverter = TypeConverterRegistry.getTypeConverterForClass(type);
		if(typeConverter == null) {
			throw new TypeConversionException("Conversion to the requested type is not supported: " + type);
					
		}
		
		return typeConverter.convert(rawCellValue);
	}

	@Override
	public URL covertToURL(String rawCellValue) throws TypeConversionException {

		try {
			if (rawCellValue == null) {
				return null;
			}

			return new URL(rawCellValue);
		} catch (MalformedURLException e) {
			throw new TypeConversionException(e);
		}
	}

	@Override
	public Timestamp covertToTimestamp(String rawCellValue, Calendar cal) throws TypeConversionException {
		// TODO: check usage of Calendar
		return covertToTimestamp(rawCellValue);
		
	}

	@Override
	public Time covertToTime(String rawCellValue, Calendar cal) throws TypeConversionException {
		// TODO: check usage of Calendar
		return covertToTime(rawCellValue);
	}

	@Override
	public Date covertToDate(String rawCellValue, Calendar cal) throws TypeConversionException {
		// TODO: check usage of Calendar
		return covertToDate(rawCellValue);
	}

	@Override
	public Reader covertToCharacterStream(String rawCellValue) throws TypeConversionException {
		if(rawCellValue == null) {
			return null;
		}
		
		return new StringReader(rawCellValue);
	}
}
