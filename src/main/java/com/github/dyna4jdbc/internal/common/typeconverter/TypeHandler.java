package com.github.dyna4jdbc.internal.common.typeconverter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public interface TypeHandler {

	ColumnMetadata getColumnMetadata();

	String covertToString(String rawCellValue) throws TypeConversionException;

	Boolean covertToBoolean(String rawCellValue) throws TypeConversionException;

	Byte covertToByte(String rawCellValue) throws TypeConversionException;

	Short covertToShort(String rawCellValue) throws TypeConversionException;

	Integer covertToInteger(String rawCellValue) throws TypeConversionException;

	Long covertToLong(String rawCellValue) throws TypeConversionException;

	Float covertToFloat(String rawCellValue) throws TypeConversionException;

	Double covertToDouble(String rawCellValue) throws TypeConversionException;

	BigDecimal covertToBigDecimal(String rawCellValue, int scale) throws TypeConversionException;

	byte[] covertToByteArray(String rawCellValue) throws TypeConversionException;

	java.sql.Date covertToDate(String rawCellValue) throws TypeConversionException;

	Time covertToTime(String rawCellValue) throws TypeConversionException;

	Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException;

	Object covertToObject(String rawCellValue, Map<String, Class<?>> map) throws TypeConversionException;

	InputStream covertToAsciiInputStream(String rawCellValue) throws TypeConversionException;

	InputStream covertToUnicodeInputStream(String rawCellValue) throws TypeConversionException;

	InputStream covertToBinaryInputStream(String rawCellValue) throws TypeConversionException;

	<T> T covertToObject(String rawCellValue, Class<T> type) throws TypeConversionException;

	URL covertToURL(String rawCellValue) throws TypeConversionException;

	Timestamp covertToTimestamp(String rawCellValue, Calendar cal) throws TypeConversionException;

	Time covertToTime(String rawCellValue, Calendar cal) throws TypeConversionException;

	java.sql.Date covertToDate(String rawCellValue, Calendar cal) throws TypeConversionException;

	Reader covertToCharacterStream(String rawCellValue) throws TypeConversionException;
	
}
