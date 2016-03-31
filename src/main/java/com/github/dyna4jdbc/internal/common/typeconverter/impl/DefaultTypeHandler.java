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

import com.github.dyna4jdbc.internal.UnexpectedIllegalStateReachedException;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;

class DefaultTypeHandler extends AbstractTypeHandler {
	
	DefaultTypeHandler(ColumnMetadata columnMetadata) {
		super(columnMetadata);
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
		
		switch (rawCellValue.trim()) {
		case "0":
			return Boolean.FALSE;
		
		case "1":
			return Boolean.TRUE;

		default:
			return Boolean.valueOf(rawCellValue);
		}
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
	public BigDecimal covertToBigDecimal(String rawCellValue) throws TypeConversionException {
		try {
			if (rawCellValue == null) {
				return null;
			}

			return new BigDecimal(rawCellValue);
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

			BigDecimal bigDecimal = new BigDecimal(rawCellValue);
			bigDecimal.setScale(scale);
			
			return bigDecimal;
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

	@Override
	public Object covertToObject(String rawCellValue, Map<String, Class<?>> map) throws TypeConversionException {
		
		SQLDataType columnType = columnMetadata.getColumnType();
		if(columnType == null) {
			throw UnexpectedIllegalStateReachedException.forMessage("columnType is null");
		}
		
		Class<?> targetClass = map.get(columnType.name);
		if(targetClass == null) {
			throw new TypeConversionException("No Java type is defined for mapping from SQL type : " + 
					columnType.name);
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
		throw new TypeConversionException("Conversion not implemented to: " + type);
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
