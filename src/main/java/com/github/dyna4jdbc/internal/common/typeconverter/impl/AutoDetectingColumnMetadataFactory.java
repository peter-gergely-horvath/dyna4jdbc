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

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

class AutoDetectingColumnMetadataFactory implements ColumnMetadataFactory {

    private static final AutoDetectingColumnMetadataFactory INSTANCE = new AutoDetectingColumnMetadataFactory();

    static AutoDetectingColumnMetadataFactory getInstance(Configuration configuration) {
        return INSTANCE;
    }


    @Override
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValues) {
        DefaultColumnMetadata columnMetaData = new DefaultColumnMetadata();
        configureFromColumnValues(columnMetaData, columnIndex, columnValues);
        return columnMetaData;
    }

    protected void configureFromColumnValues(DefaultColumnMetadata metaData,
                                      int columnIndex, Iterable<String> cellValues) {

        final int sqlColumnIndex = columnIndex + 1;

        TypeDetector.DetectionResult detected = TypeDetector.detectColumnType(cellValues);

        metaData.setConsumesFirstRow(false);
        metaData.setCurrency(detected.isCurrency());
        metaData.setNullability(detected.getNullability());
        metaData.setSigned(detected.isSigned());
        metaData.setColumnLabel(String.valueOf(sqlColumnIndex));
        metaData.setColumnName(String.valueOf(sqlColumnIndex));
        metaData.setPrecision(detected.getMaxPrecision());
        metaData.setScale(detected.getMaxScale());
        metaData.setColumnDisplaySize(detected.getMaxColumnDisplaySize());
        metaData.setColumnType(detected.getColumnType());
    }


}
