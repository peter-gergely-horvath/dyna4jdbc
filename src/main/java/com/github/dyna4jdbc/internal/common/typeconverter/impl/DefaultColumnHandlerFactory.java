package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandler;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

public final class DefaultColumnHandlerFactory implements ColumnHandlerFactory {

    private final ColumnMetadataFactory columnMetadataFactory;

    private DefaultColumnHandlerFactory(Configuration configuration) {
        this.columnMetadataFactory = new DefaultColumnMetadataFactory(configuration);
    }

    public static DefaultColumnHandlerFactory getInstance(Configuration configuration) {
        return new DefaultColumnHandlerFactory(configuration);
    }

    @Override
    public ColumnHandler newTypeHandler(
            int columnNumber, Iterable<String> columnIterable) {

        ColumnMetadata columnMetadata = columnMetadataFactory.getColumnMetadata(columnNumber, columnIterable);

        String formatString = columnMetadata.getFormatString();
        if (formatString != null) {
            return createFormatStringBaseTypeHandler(columnMetadata, formatString);
        }

        return new DefaultColumnHandler(columnMetadata);
    }

    private static ColumnHandler createFormatStringBaseTypeHandler(
            ColumnMetadata columnMetadata, String formatString) {

        SQLDataType columnType = columnMetadata.getColumnType();
        if (columnType == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "columnType is null");
        }

        switch (columnType) {
        case TIMESTAMP:
        case TIMESTAMP_WITH_TIMEZONE:
            return new TimestamptFormatStringColumnHandler(columnMetadata, formatString);

        default:
            throw JDBCError.FORMAT_STRING_UNEXPECTED_FOR_COLUMN_TYPE.raiseUncheckedException(
                    formatString, columnType);
        }
    }
}
