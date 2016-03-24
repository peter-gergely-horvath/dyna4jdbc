package com.github.dyna4jdbc.internal.common.typeconverter;

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

	java.math.BigDecimal covertToBigDecimal(String rawCellValue) throws TypeConversionException;
	
	java.math.BigDecimal covertToBigDecimal(String rawCellValue, int scale) throws TypeConversionException;

	byte[] covertToByteArray(String rawCellValue) throws TypeConversionException;

	java.sql.Date covertToDate(String rawCellValue) throws TypeConversionException;

	java.sql.Time covertToTime(String rawCellValue) throws TypeConversionException;

	java.sql.Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException;

	Object covertToObject(String rawCellValue, java.util.Map<String, Class<?>> map) throws TypeConversionException;

	java.io.InputStream covertToAsciiInputStream(String rawCellValue) throws TypeConversionException;

	java.io.InputStream covertToUnicodeInputStream(String rawCellValue) throws TypeConversionException;

	java.io.InputStream covertToBinaryInputStream(String rawCellValue) throws TypeConversionException;

	<T> T covertToObject(String rawCellValue, Class<T> type) throws TypeConversionException;

	java.net.URL covertToURL(String rawCellValue) throws TypeConversionException;

	java.sql.Timestamp covertToTimestamp(String rawCellValue, java.util.Calendar cal) throws TypeConversionException;

	java.sql.Time covertToTime(String rawCellValue, java.util.Calendar cal) throws TypeConversionException;

	java.sql.Date covertToDate(String rawCellValue, java.util.Calendar cal) throws TypeConversionException;

	java.io.Reader covertToCharacterStream(String rawCellValue) throws TypeConversionException;
}
