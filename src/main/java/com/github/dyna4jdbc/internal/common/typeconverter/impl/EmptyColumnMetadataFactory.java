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

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.config.Configuration;

final class EmptyColumnMetadataFactory implements ColumnMetadataFactory {

    private static final int DEFAULT_COLUMN_DISPLAY_SIZE = 4;

    private static final EmptyColumnMetadataFactory INSTANCE = new EmptyColumnMetadataFactory();

    static EmptyColumnMetadataFactory getInstance(Configuration configuration) {
        return INSTANCE;
    }

    private EmptyColumnMetadataFactory() {
    }

    @Override
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValues) {
        
        final int sqlColumnIndex = columnIndex + 1;
        
        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRow(false);
        metadata.setCurrency(false);
        metadata.setNullability(Nullability.UNKNOWN);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(DEFAULT_COLUMN_DISPLAY_SIZE);
        metadata.setColumnLabel(String.valueOf(sqlColumnIndex));
        metadata.setColumnName(String.valueOf(sqlColumnIndex));
        metadata.setPrecision(0);
        metadata.setScale(0);
        metadata.setColumnType(SQLDataType.VARCHAR);

        return metadata;
    }

}
