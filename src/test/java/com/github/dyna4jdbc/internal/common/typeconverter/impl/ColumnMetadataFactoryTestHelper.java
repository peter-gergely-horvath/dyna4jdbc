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

/**
 * @author Peter G. Horvath
 */
class ColumnMetadataFactoryTestHelper {

    private ColumnMetadataFactoryTestHelper() {
        // static utility class
    }

    static final String TEST_COLUMN_INDEX = "1";

    static ColumnMetadata varcharColumnMetadata(
            int scale, String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRow(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setNullability(nullability);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.VARCHAR);
        metadata.setFormatString(null);

        return metadata;
    }

    static ColumnMetadata integerColumnMetadata(
            int scale, String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRow(false);
        metadata.setColumnDisplaySize(scale);
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(scale);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.INTEGER);
        metadata.setFormatString(null);

        return metadata;
    }

    static ColumnMetadata doubleColumnMetadata(
            int scale, int precision, String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        final int expectedColumnDisplaySize;
        if(precision == 0) {
            expectedColumnDisplaySize = scale;
        } else {
            // include decimal point in ColumnDisplaySize
            expectedColumnDisplaySize =  scale + 1;
        }

        metadata.setConsumesFirstRow(false);
        metadata.setNullability(nullability);
        metadata.setSigned(true);
        metadata.setColumnDisplaySize(expectedColumnDisplaySize);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(precision);
        metadata.setScale(scale);
        metadata.setColumnType(SQLDataType.DOUBLE);
        metadata.setFormatString(null);

        return metadata;
    }

    static ColumnMetadata timestampColumnMetadata(
            String header, ColumnMetadata.Nullability nullability) {

        DefaultColumnMetadata metadata = new DefaultColumnMetadata();

        metadata.setConsumesFirstRow(false);
        metadata.setNullability(nullability);
        metadata.setSigned(false);
        metadata.setColumnDisplaySize(23);
        metadata.setColumnLabel(header);
        metadata.setColumnName(header);
        metadata.setPrecision(0);
        metadata.setScale(0);
        metadata.setColumnType(SQLDataType.TIMESTAMP);
        metadata.setFormatString(null);

        return metadata;
    }
}
