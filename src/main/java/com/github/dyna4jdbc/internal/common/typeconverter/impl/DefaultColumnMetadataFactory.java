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

import java.util.Iterator;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

final class DefaultColumnMetadataFactory implements ColumnMetadataFactory {

    // "Pattern: Instances of this class are immutable and are safe for use by
    // multiple concurrent threads."
    private static final Pattern HEADER_PATTERN = Pattern.compile(buildMatchPattern());


    private final EmptyColumnMetadataFactory emptyColumnMetadataFactory;
    private final HeuristicsColumnMetadataFactory heuristicsColumnMetadataFactory;
    private final ColumnHeaderColumnMetadataFactory columnHeaderColumnMetadataFactory;

    DefaultColumnMetadataFactory(Configuration configuration) {
        emptyColumnMetadataFactory = EmptyColumnMetadataFactory.getInstance(configuration);
        heuristicsColumnMetadataFactory = HeuristicsColumnMetadataFactory.getInstance(configuration);
        columnHeaderColumnMetadataFactory = ColumnHeaderColumnMetadataFactory.getInstance(configuration);

    }

    @Override
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValues) {

        Iterator<String> iterator = columnValues.iterator();

        if (!iterator.hasNext()) {
            return emptyColumnMetadataFactory.getColumnMetadata(columnIndex, columnValues);
        }

        final String firstValueFromColumn = iterator.next();

        final boolean headerSeemsToContainParseInstructions =
                firstValueFromColumn != null && HEADER_PATTERN.matcher(firstValueFromColumn).matches();

        if (headerSeemsToContainParseInstructions) {
            return columnHeaderColumnMetadataFactory.getColumnMetadata(columnIndex, columnValues);
        } else {
            return heuristicsColumnMetadataFactory.getColumnMetadata(columnIndex, columnValues);
        }

    }

    private static String buildMatchPattern() {

        StringBuilder sqlTypeNamesSeparatedByPipeForRegex = new StringBuilder();
        boolean isFirst = true;
        for (SQLDataType sqlDataType : SQLDataType.values()) {
            String sqlTypeName = sqlDataType.name;
            if (isFirst) {
                isFirst = false;
            } else {
                sqlTypeNamesSeparatedByPipeForRegex.append("|");
            }

            sqlTypeNamesSeparatedByPipeForRegex.append(sqlTypeName);
        }

        return String.format("^[^:]+:((?:%s)[^:]*)?:[^:]*$", sqlTypeNamesSeparatedByPipeForRegex);
    }


}
