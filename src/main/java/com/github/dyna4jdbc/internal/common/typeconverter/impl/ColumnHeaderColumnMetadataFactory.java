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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.collection.AlwaysSkipFirstElementIterable;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.ConfigurationStringParser;

final class ColumnHeaderColumnMetadataFactory extends HeuristicsColumnMetadataFactory {

    private static final Pattern SQL_TYPE_PATTERN =
            Pattern.compile("\\s*(\\w+)(?:\\s*[(]\\s*(\\d+)\\s*[,]?\\s*(\\d)?\\s*[)])?\\s*");

    private static final int MATCHER_GROUP_INDEX_SQL_TYPE = 1;
    private static final int MATCHER_GROUP_SCALE_PART = 2;
    private static final int MATCHER_GROUP_PRECISION_PART = 3;

    private static final ColumnHeaderColumnMetadataFactory INSTANCE = new ColumnHeaderColumnMetadataFactory();


    private ColumnHeaderColumnMetadataFactory() {

    }

    static ColumnHeaderColumnMetadataFactory getInstance(Configuration configuration) {
        return INSTANCE;
    }

    protected void configureFromColumnValues(DefaultColumnMetadata metaData,
                                      int columnIndex, Iterable<String> columnValuesIterable) {


        Iterator<String> iterator = columnValuesIterable.iterator();

        if (!iterator.hasNext()) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "iterator is empty: could not extract header value");
        }

        String firstValue = iterator.next();
        String[] configStringArray = firstValue.split(":", 3);

        String header = ArrayUtils.tryGetByIndex(configStringArray, 0);
        String sqlTypeConfig = ArrayUtils.tryGetByIndex(configStringArray, 1);
        String metaDataConfig = ArrayUtils.tryGetByIndex(configStringArray, 2);

        super.configureFromColumnValues(metaData, columnIndex, 
                new AlwaysSkipFirstElementIterable<>(columnValuesIterable));


        if (header != null && !"".equals(header.trim())) {
            configureHeader(metaData, header);
        }
        if (sqlTypeConfig != null && !"".equals(sqlTypeConfig.trim())) {
            configureSqlType(metaData, sqlTypeConfig, columnIndex);
        }
        if (metaDataConfig != null && !"".equals(metaDataConfig.trim())) {
            configureAdditional(metaData, metaDataConfig, columnIndex);
        }

        metaData.setConsumesFirstRow(true);

    }

    private void configureHeader(DefaultColumnMetadata metaData, String header) {
        metaData.setColumnLabel(header);
        metaData.setColumnName(header);
    }

    private void configureSqlType(DefaultColumnMetadata metaData, String sqlTypeConfig, int columnIndex) {

        try {
            Matcher matcher = SQL_TYPE_PATTERN.matcher(sqlTypeConfig);
            if (!matcher.matches()) {
                throw JDBCError.INVALID_FORMATTING_HEADER.raiseSQLException(sqlTypeConfig);
            }

            String sqlTypePart = matcher.group(MATCHER_GROUP_INDEX_SQL_TYPE);
            String scalePart = matcher.group(MATCHER_GROUP_SCALE_PART);
            String precisionPart = matcher.group(MATCHER_GROUP_PRECISION_PART);


            SQLDataType sqlDataType = SQLDataType.valueOf(sqlTypePart.toUpperCase(Locale.ENGLISH));
            metaData.setColumnType(sqlDataType);

            if (scalePart != null) {
                metaData.setScale(Integer.parseInt(scalePart));
            } else {
                metaData.setScale(0);
            }

            if (precisionPart != null) {
                metaData.setPrecision(Integer.parseInt(precisionPart));
            } else {
                metaData.setPrecision(0);
            }


        } catch (SQLException | RuntimeException e) {
            final int jdbcColumnIndex = columnIndex + 1;

            throw JDBCError.INVALID_FORMATTING_HEADER.raiseUncheckedException(e,
                    jdbcColumnIndex, sqlTypeConfig, e.getMessage());
        }

    }

    private void configureAdditional(DefaultColumnMetadata metaData, String metaDataConfig, int columnIndex) {
        try {
            Properties props = ConfigurationStringParser.getInstance().parseStringToProperties(metaDataConfig);


            for (AdditionalColumnConfiguration acc : AdditionalColumnConfiguration.values()) {
                String property = props.getProperty(acc.getKey(), null);
                acc.setConfiguration(metaData, property);
            }

        } catch (MisconfigurationException ex) {
            final int jdbcColumnIndex = columnIndex + 1;

            throw JDBCError.INVALID_FORMATTING_HEADER.raiseUncheckedException(ex,
                    jdbcColumnIndex, metaDataConfig, ex.getMessage());
        }
    }


}
