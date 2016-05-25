
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
import com.github.dyna4jdbc.internal.config.impl.ConfigurationStringParser;

final class ColumnHeaderColumnMetadataFactory extends HeuristicsColumnMetadataFactory {

    private static final Pattern SQL_TYPE_PATTERN =
            Pattern.compile("\\s*(\\w+)(?:\\s*[(]\\s*(\\d+)\\s*[,]?\\s*(\\d)?\\s*[)])?\\s*");

    private static final int MATCHER_GROUP_INDEX_SQL_TYPE = 1;
    private static final int MATCHER_GROUP_SCALE_PART = 2;
    private static final int MATCHER_GROUP_PRECISION_PART = 3;


    ColumnHeaderColumnMetadataFactory(Configuration configuration) {

    }

    static ColumnHeaderColumnMetadataFactory getInstance(Configuration configuration) {
        return new ColumnHeaderColumnMetadataFactory(configuration);
    }

    protected void configureForValues(DefaultColumnMetadata metaData,
                                      int columnIndex, Iterable<String> columnValuesIterable) {


        Iterator<String> iterator = columnValuesIterable.iterator();

        if (!iterator.hasNext()) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "iterator is empty: could not extract header value");
        }

        String firstValue = iterator.next();
        String[] configStringArray = firstValue.split(":");

        String header = ArrayUtils.tryGetByIndex(configStringArray, 0);
        String sqlTypeConfig = ArrayUtils.tryGetByIndex(configStringArray, 1);
        String metaDataConfig = ArrayUtils.tryGetByIndex(configStringArray, 2);

        super.configureForValues(metaData, columnIndex, new AlwaysSkipFirstElementIterable<>(columnValuesIterable));


        if (header != null && !"".equals(header.trim())) {
            configureHeader(metaData, header);
        }
        if (sqlTypeConfig != null && !"".equals(sqlTypeConfig.trim())) {
            configureSqlType(metaData, sqlTypeConfig);
        }
        if (metaDataConfig != null && !"".equals(metaDataConfig.trim())) {
            configureAdditional(metaData, metaDataConfig);
        }

        metaData.setTakesFirstRowValue(true);

    }

    private void configureHeader(DefaultColumnMetadata metaData, String header) {
        metaData.setColumnLabel(header);
        metaData.setColumnName(header);
    }

    private void configureSqlType(DefaultColumnMetadata metaData, String sqlTypeConfig) {

        try {
            Matcher matcher = SQL_TYPE_PATTERN.matcher(sqlTypeConfig);
            if (!matcher.matches()) {
                throw JDBCError.INVALID_CONFIGURATION_HEADER.raiseSQLException(sqlTypeConfig);
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
            throw new IllegalStateException("Processing of header failed: " + sqlTypeConfig + "; " + e.getMessage(), e);
        }

    }

    private void configureAdditional(DefaultColumnMetadata metaData, String metaDataConfig) {
        Properties props = ConfigurationStringParser.getInstance().parseStringToProperties(metaDataConfig);
        String formatString = props.getProperty("format"); // TODO: clean this up!
        metaData.setFormatString(formatString);
    }


}
