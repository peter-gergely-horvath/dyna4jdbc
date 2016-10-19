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
    private DateFormat formatter;

    TimestampFormatStringColumnHandler(ColumnMetadata columnMetadata, String formatString) {
        super(columnMetadata);

        try {
            this.formatString = formatString;
            this.formatter = new SimpleDateFormat(formatString);
            this.formatter.setLenient(false);
        } catch (IllegalArgumentException e) {
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

    public Timestamp covertToTimestamp(String rawCellValue) throws TypeConversionException {

        try {
            Date parsedDate = formatter.parse(rawCellValue);
            return new Timestamp(parsedDate.getTime());

        } catch (ParseException e) {
            throw new TypeConversionException("Could not parse '" + rawCellValue
                            + "' according to format string '" + formatString + "'", e);

        }
    }

}
