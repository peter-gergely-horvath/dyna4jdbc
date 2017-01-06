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

import java.util.Arrays;
import java.util.List;

import static com.github.dyna4jdbc.internal.common.typeconverter.impl.ColumnMetadataFactoryTestHelper.*;

/**
 * @author Peter G. Horvath
 */
enum HeuristicsColumnMetadataFactoryTestParameters {

    NULLS("Nulls",
            varcharColumnMetadata(0, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "<null>", "<null>", "<null>", "<null>"),

    NULLS_AND_EMPTY_STRINGS("Nulls and empty strings",
            varcharColumnMetadata(0, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "<null>", "", "<null>", ""),

    VARIABLE_LENGTH_STRINGS("Strings with variable length",
            varcharColumnMetadata(6, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "Mary", "had", "a", "little", "lamb"),

    VARIABLE_LENGTH_STRINGS_AND_NULLS("Strings with variable length and nulls",
            varcharColumnMetadata(6, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "Mary", "had", "<null>", "a", "little", "<null>", "lamb"),

    VARIABLE_LENGTH_STRINGS_AND_EMPTY_STRINGS("Strings with variable length and empty strings",
            varcharColumnMetadata(6, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "Mary", "had", "", "a", "little", "", "lamb"),

    INTEGERS("Integers",
            integerColumnMetadata(4, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "13", "42", "123", "561", "1984"),

    INTEGERS_AND_NULLS("Integers and nulls",
            integerColumnMetadata(4, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "13", "42", "<null>", "123", "561", "<null>", "1984"),

    INTEGERS_AND_STRINGS("Integers and strings",
            varcharColumnMetadata(4, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "13", "42", "<null>", "123", "561", "<null>", "1984", "Foo", "Bar"),

    DOUBLES("Doubles",
            doubleColumnMetadata(13, 9, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "0.1234", "1.234", "12.34", "123.4", "1984.123456789"),

    DOUBLES_AND_STRINGS("Doubles and strings",
            varcharColumnMetadata(6, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "0.1234", "1.234", "12.34", "Mary", "had", "a", "little", "lamb"),

    DOUBLES_AND_INTEGERS("Doubles and integers",
            doubleColumnMetadata(13, 4, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "0.1234", "1.234", "12.34", "123.4", "123456789"),

    DOUBLES_INTEGERS_AND_NULLS("Doubles and integers and nulls",
            doubleColumnMetadata(18, 9, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "0.123456789", "1.234", "<null>", "12.34", "123.4", "<null>", "123456789"),

    TIMESTAMP("Timestamp",
            timestampColumnMetadata(TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NOT_NULLABLE),
            "2016-06-01 19:16:19.123", "2016-06-01 19:16:19"),

    TIMESTAMP_AND_NULLS("Timestamp",
            timestampColumnMetadata(TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "2016-06-01 19:16:19.123", "<null>", "2016-06-01 19:16:19", "<null>"),

    TIMESTAMP_AND_INTEGERS_AND_NULLS("Timestamp, integers and nulls",
            varcharColumnMetadata(23, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "<null>", "2016-06-01 19:16:19.123", "2016-06-01 19:16:19", "13", "<null>", "42" ),

    TIMESTAMP_AND_DOUBLES_AND_NULLS("Timestamp, doubles and nulls",
            varcharColumnMetadata(23, TEST_COLUMN_INDEX, ColumnMetadata.Nullability.NULLABLE),
            "<null>", "2016-06-01 19:16:19.123", "2016-06-01 19:16:19", "1.234", "<null>", "123456789.0" );




    private final ColumnMetadata expectedColumnMetadata;

    private final String description;
    private final List<String> inputValues;

    HeuristicsColumnMetadataFactoryTestParameters(
            String description,
            ColumnMetadata expectedColumnMetadata,
            String... inputValues) {

        this.description = description;
        this.expectedColumnMetadata = expectedColumnMetadata;
        this.inputValues = Arrays.asList(inputValues);
    }

    @Override
    public String toString() {
        return description;
    }


    List<String> getInputValues() {
        return inputValues;
    }

    ColumnMetadata getExpectedColumnMetadata() {
        return expectedColumnMetadata;
    }
}
