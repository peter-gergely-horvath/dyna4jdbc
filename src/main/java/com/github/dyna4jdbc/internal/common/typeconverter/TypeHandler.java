package com.github.dyna4jdbc.internal.common.typeconverter;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

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

	Date covertToDate(String rawCellValue) throws TypeConversionException;

	Time covertToTime(String rawCellValue) throws TypeConversionException;

	Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException;
	
}
