/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.common.typeconverter;

public interface ColumnHandler {

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
