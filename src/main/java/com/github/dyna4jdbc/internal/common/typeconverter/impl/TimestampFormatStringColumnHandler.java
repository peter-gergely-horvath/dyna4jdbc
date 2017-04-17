/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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

 
package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeConversionException;

class TimestampFormatStringColumnHandler extends DefaultColumnHandler {

    private final String formatString;
    private final DateFormat formatter;

    TimestampFormatStringColumnHandler(ColumnMetadata columnMetadata, String formatString) {
        super(columnMetadata);

        try {
            if (formatString == null || formatString.trim().equals("")) {
                throw new IllegalArgumentException("formatString is REQUIRED. It cannot be empty / whitespace only");
            }

            this.formatString = formatString;
            this.formatter = new SimpleDateFormat(formatString);
            this.formatter.setLenient(false);
        } catch (IllegalArgumentException e) {
            /*
             NOTE: IllegalArgumentException can be both thrown from our
             formatString checking logic AND SimpleDateFormat as well!
             */
            throw JDBCError.FORMAT_STRING_INVALID.raiseUncheckedException(
                    e, formatString, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T covertToObject(String rawCellValue, Class<T> type) throws TypeConversionException {

        if (type == Timestamp.class) {
            return (T) covertToTimestamp(rawCellValue);
        } else {
            return super.covertToObject(rawCellValue, type);
        }
    }

    @Override
    public Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException {

        try {
            Date parsedDate = formatter.parse(rawCellValue);
            return new Timestamp(parsedDate.getTime());

        } catch (ParseException e) {
            throw new TypeConversionException("Could not parse '" + rawCellValue
                            + "' according to format string '" + formatString + "'", e);

        }
    }

    @Override
    public java.sql.Date covertToDate(String rawCellValue) throws TypeConversionException {

        try {
            Date parsedDate = formatter.parse(rawCellValue);
            return new java.sql.Date(parsedDate.getTime());

        } catch (ParseException e) {
            throw new TypeConversionException("Could not parse '" + rawCellValue
                    + "' according to format string '" + formatString + "'", e);

        }
    }

}
