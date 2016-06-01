package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadataFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata.Nullability;
import com.github.dyna4jdbc.internal.config.Configuration;

class HeuristicsColumnMetadataFactory implements ColumnMetadataFactory {

    private static final HeuristicsColumnMetadataFactory INSTANCE = new HeuristicsColumnMetadataFactory();

    static HeuristicsColumnMetadataFactory getInstance(Configuration configuration) {
        return INSTANCE;
    }


    @Override
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValuesIterable) {
        DefaultColumnMetadata columnMetaData = new DefaultColumnMetadata();
        configureForValues(columnMetaData, columnIndex, columnValuesIterable);
        return columnMetaData;
    }

    protected void configureForValues(DefaultColumnMetadata metaData,
                                      int columnIndex, Iterable<String> cellValues) {

        final int sqlColumnIndex = columnIndex + 1;

        int maxSize = 0;
        int maxPrecision = 0;
        int maxScale = 0;
        int maxBeforeDecimalPoint = 0;

        Nullability nullability = Nullability.NOT_NULLABLE;

        SQLDataType columnType = SQLDataType.OTHER;

        for (String cellValue : cellValues) {

            columnType = getColumnTypeByCurrentlySelectedTypeAndCellValue(columnType, cellValue);

            if (cellValue == null) {
                nullability = Nullability.NULLABLE;
            } else {

                final int scale = columnType.getScale(cellValue);
                final int precision = columnType.getPrecision(cellValue);

                maxBeforeDecimalPoint = Math.max(maxBeforeDecimalPoint, scale - precision);

                maxSize = Math.max(maxSize, cellValue.length());

                maxPrecision = Math.max(maxPrecision, precision);


                maxScale = Math.max(maxScale, scale);
            }
        }

        int maxColumnDisplaySize;
        if (maxPrecision == 0) {
            maxColumnDisplaySize = maxSize;
        } else {
            maxColumnDisplaySize = maxBeforeDecimalPoint + 1 + maxPrecision;

            maxScale = Math.max(maxScale, maxBeforeDecimalPoint + maxPrecision);
        }

        if (maxPrecision > 0 && columnType == SQLDataType.VARCHAR) {
            maxScale = maxSize;
            maxColumnDisplaySize = maxSize;
            maxPrecision = 0;
        }

        metaData.setConsumesFirstRowValue(false);
        metaData.setCurrency(false);
        metaData.setNullability(nullability);
        metaData.setSigned(columnType != SQLDataType.VARCHAR);
        metaData.setColumnLabel(String.valueOf(sqlColumnIndex));
        metaData.setColumnName(String.valueOf(sqlColumnIndex));
        metaData.setPrecision(maxPrecision);
        metaData.setScale(maxScale);
        metaData.setColumnDisplaySize(maxColumnDisplaySize);
        metaData.setColumnType(columnType);
    }

    private static SQLDataType getColumnTypeByCurrentlySelectedTypeAndCellValue(
            SQLDataType currentColumnType, String cellValue) {

        SQLDataType newColumnType = currentColumnType;

        switch (currentColumnType) {
            case OTHER:
                if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.DOUBLE;
                    break;
                }
                if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.INTEGER;
                    break;
                }
                if (SQLDataType.TIMESTAMP.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.TIMESTAMP;
                    break;
                }

                if (SQLDataType.VARCHAR.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.VARCHAR;
                    break;
                }

                break;

            case INTEGER:
                if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.INTEGER;
                    break;
                }

            case DOUBLE:
                if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)
                        || SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.DOUBLE;
                    break;
                }

            case TIMESTAMP:
                if (SQLDataType.TIMESTAMP.isPlausibleConversion(cellValue)) {
                    newColumnType = SQLDataType.TIMESTAMP;
                    break;
                }

            case VARCHAR:
                newColumnType = SQLDataType.VARCHAR;
                break;

            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "Unexpected columnType: " + newColumnType);
        }
        return newColumnType;
    }


}
