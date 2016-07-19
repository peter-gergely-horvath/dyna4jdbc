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
    public ColumnMetadata getColumnMetadata(int columnIndex, Iterable<String> columnValues) {
        DefaultColumnMetadata columnMetaData = new DefaultColumnMetadata();
        configureForValues(columnMetaData, columnIndex, columnValues);
        return columnMetaData;
    }

    private static class DetectionContext {

        private SQLDataType columnType = SQLDataType.OTHER;
        private int maxSize = 0;
        private int maxPrecision = 0;
        private int maxScale = 0;
        private int maxBeforeDecimalPoint = 0;
        private Nullability nullability = Nullability.NOT_NULLABLE;
        private boolean signed = false;
        private boolean currency = false;

        private int maxColumnDisplaySize = 0;

    }

    protected void configureForValues(DefaultColumnMetadata metaData,
                                      int columnIndex, Iterable<String> cellValues) {

        final int sqlColumnIndex = columnIndex + 1;

        DetectionContext detectionContext = new DetectionContext();


        for (String cellValue : cellValues) {

            detectMetadataByInspectingCellValue(detectionContext, cellValue);
        }


        if (detectionContext.maxPrecision == 0) {
            detectionContext.maxColumnDisplaySize = detectionContext.maxSize;
        } else {
            detectionContext.maxColumnDisplaySize =
                    detectionContext.maxBeforeDecimalPoint + 1 + detectionContext.maxPrecision;

            detectionContext.maxScale = Math.max(detectionContext.maxScale,
                    detectionContext.maxBeforeDecimalPoint + detectionContext.maxPrecision);
        }

        if (detectionContext.maxPrecision > 0 && detectionContext.columnType == SQLDataType.VARCHAR) {
            detectionContext.maxScale = detectionContext.maxSize;
            detectionContext.maxColumnDisplaySize = detectionContext.maxSize;
            detectionContext.maxPrecision = 0;
        }

        metaData.setConsumesFirstRowValue(false);
        metaData.setCurrency(detectionContext.currency);
        metaData.setNullability(detectionContext.nullability);
        metaData.setSigned(detectionContext.signed);
        metaData.setColumnLabel(String.valueOf(sqlColumnIndex));
        metaData.setColumnName(String.valueOf(sqlColumnIndex));
        metaData.setPrecision(detectionContext.maxPrecision);
        metaData.setScale(detectionContext.maxScale);
        metaData.setColumnDisplaySize(detectionContext.maxColumnDisplaySize);
        metaData.setColumnType(detectionContext.columnType);
    }

    private static void detectMetadataByInspectingCellValue(DetectionContext detectionContext, String cellValue) {

        if (cellValue == null) {
            detectionContext.nullability = Nullability.NULLABLE;
        }

        switch (detectionContext.columnType) {
            case OTHER:
                if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)) {
                    handleDoubleType(detectionContext, cellValue);
                    break;
                }
                if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
                    handleInteger(detectionContext, cellValue);
                    break;
                }
                if (SQLDataType.TIMESTAMP.isPlausibleConversion(cellValue)) {
                    handleTimestamp(detectionContext, cellValue);
                    break;
                }

                if (SQLDataType.VARCHAR.isPlausibleConversion(cellValue)) {
                    handleVarChar(detectionContext, cellValue);
                    break;
                }

                break;

            case INTEGER:
                if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
                    handleInteger(detectionContext, cellValue);
                    break;
                }

            case DOUBLE:
                if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)
                        || SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {

                    handleDoubleType(detectionContext, cellValue);
                    break;
                }

            case TIMESTAMP:
                if (SQLDataType.TIMESTAMP.isPlausibleConversion(cellValue)) {
                    handleTimestamp(detectionContext, cellValue);
                    break;
                }

            case VARCHAR:
                handleVarChar(detectionContext, cellValue);
                break;

            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "Unexpected column type: " + detectionContext.columnType);
        }


    }


    private static void handleInteger(DetectionContext detected, String cellValue) {
        detected.columnType = SQLDataType.INTEGER;
        detected.signed = true;

        if (cellValue != null) {
            final int scale = detected.columnType.getScale(cellValue);

            detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, scale);
            detected.maxSize = Math.max(detected.maxSize, scale);
            detected.maxScale = Math.max(detected.maxScale, scale);
            detected.maxPrecision = 0;
        }
    }

    private static void handleDoubleType(DetectionContext detected, String cellValue) {

        if (detected.columnType == SQLDataType.DOUBLE) {
            if (cellValue != null) {
                final int scale = detected.columnType.getScale(cellValue);
                final int precision = detected.columnType.getPrecision(cellValue);

                detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, scale - precision);
                detected.maxPrecision = Math.max(detected.maxPrecision, precision);

                detected.maxSize = Math.max(detected.maxSize, cellValue.length());

                detected.maxScale = Math.max(detected.maxScale, scale);
            }
        } else {

            detected.columnType = SQLDataType.DOUBLE;
            detected.signed = true;

            final int scale = detected.columnType.getScale(cellValue);
            final int precision = detected.columnType.getPrecision(cellValue);

            detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, scale - precision);
            detected.maxSize = Math.max(detected.maxSize, cellValue.length());
            detected.maxPrecision = Math.max(detected.maxPrecision, precision);
            detected.maxScale = Math.max(detected.maxScale, scale);

        }
    }

    private static void handleTimestamp(DetectionContext detected, String cellValue) {
        detected.columnType = SQLDataType.TIMESTAMP;
        detected.maxScale = 0;
        detected.signed = false;

        if (cellValue != null) {
            detected.maxSize = Math.max(detected.maxSize, cellValue.length());
            detected.maxBeforeDecimalPoint = 0;
            detected.maxScale = 0;
            detected.maxPrecision = 0;
        }
    }

    private static void handleVarChar(DetectionContext detected, String cellValue) {
        detected.columnType = SQLDataType.VARCHAR;
        detected.signed = false;
        detected.maxPrecision = 0;

        if (cellValue != null) {
            detected.maxSize = Math.max(detected.maxSize, cellValue.length());
            detected.maxScale = Math.max(detected.maxScale, detected.maxSize);
            detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, detected.maxSize);
            detected.maxPrecision = 0;
        }
    }

}
