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
    public ColumnHandler newColumnHandler(
            int columnNumber, Iterable<String> columnValues) {

        ColumnMetadata columnMetadata = columnMetadataFactory.getColumnMetadata(columnNumber, columnValues);

        String formatString = columnMetadata.getFormatString();
        if (formatString != null) {
            return createFormatStringBasedTypeHandler(columnMetadata, formatString);
        }

        return new DefaultColumnHandler(columnMetadata);
    }

    private static ColumnHandler createFormatStringBasedTypeHandler(
            ColumnMetadata columnMetadata, String formatString) {

        SQLDataType columnType = columnMetadata.getColumnType();
        if (columnType == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "columnType is null");
        }

        switch (columnType) {
        case TIMESTAMP:
        case TIMESTAMP_WITH_TIMEZONE:
            return new TimestampFormatStringColumnHandler(columnMetadata, formatString);

        default:
            throw JDBCError.FORMAT_STRING_UNEXPECTED_FOR_COLUMN_TYPE.raiseUncheckedException(
                    formatString, columnType);
        }
    }
}
