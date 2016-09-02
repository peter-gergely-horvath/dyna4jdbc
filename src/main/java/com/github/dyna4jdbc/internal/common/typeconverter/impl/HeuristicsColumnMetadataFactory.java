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

        if (detectionContext.columnType == SQLDataType.OTHER) {
            detectionContext.columnType = SQLDataType.VARCHAR;
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

        metaData.setConsumesFirstRow(false);
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
                leaveOther(detectionContext, cellValue);
                break;

            case INTEGER:
                if (inspectInteger(detectionContext, cellValue)) {
                    break;
                }

            case DOUBLE:
                inspectDouble(detectionContext, cellValue);
                break;

            case TIMESTAMP:
                leaveTimestamp(detectionContext, cellValue);
                break;

            case VARCHAR:
                leaveVarChar(detectionContext, cellValue);
                break;

            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "Unexpected column type: " + detectionContext.columnType);
        }
    }

    private static void leaveVarChar(DetectionContext detectionContext, String cellValue) {
        enterVarChar(detectionContext, cellValue);
    }

    private static void leaveTimestamp(DetectionContext detectionContext, String cellValue) {
        if (SQLDataType.TIMESTAMP.isPlausibleConversion(cellValue)) {
            
            // transition Timestamp --> Timestamp
            enterTimestamp(detectionContext, cellValue);
            
        } else {

            // transition Timestamp --> VarChar
            enterVarChar(detectionContext, cellValue);

        }
    }

    private static void inspectDouble(DetectionContext detectionContext, String cellValue) {
        if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)
                || SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {

            // transition Double --> Double
            enterDouble(detectionContext, cellValue);
        } else {

            // transition Double --> VarChar
            enterVarChar(detectionContext, cellValue);
        }
    }

    private static void leaveOther(DetectionContext detectionContext, String cellValue) {

        if (cellValue == null) {
            return; // NO-OP: transition Other --> Other
        } else if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
            enterInteger(detectionContext, cellValue);
        } else if (SQLDataType.DOUBLE.isPlausibleConversion(cellValue)) {
            enterDouble(detectionContext, cellValue);
        } else if (SQLDataType.TIMESTAMP.isPlausibleConversion(cellValue)) {
            enterTimestamp(detectionContext, cellValue);
        } else if (SQLDataType.VARCHAR.isPlausibleConversion(cellValue)) {
            enterVarChar(detectionContext, cellValue);
        } else {
            JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException("no matching type for value: " + cellValue);
        }
    }

    private static boolean inspectInteger(DetectionContext detectionContext, String cellValue) {
        if (SQLDataType.INTEGER.isPlausibleConversion(cellValue)) {
            enterInteger(detectionContext, cellValue);
            return true;
        }
        return false;
    }


    private static void enterInteger(DetectionContext detected, String cellValue) {
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

    private static void enterDouble(DetectionContext detected, String cellValue) {

        detected.columnType = SQLDataType.DOUBLE;
        detected.signed = true;

        if (cellValue != null) {
            final int scale = detected.columnType.getScale(cellValue);
            final int precision = detected.columnType.getPrecision(cellValue);

            detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, scale - precision);
            detected.maxPrecision = Math.max(detected.maxPrecision, precision);

            detected.maxSize = Math.max(detected.maxSize, cellValue.length());

            detected.maxScale = Math.max(detected.maxScale, scale);
        }
    }

    private static void enterTimestamp(DetectionContext detected, String cellValue) {

        final SQLDataType previousColumnType = detected.columnType;
        switch (previousColumnType) {
            case OTHER:
            case TIMESTAMP:

                detected.columnType = SQLDataType.TIMESTAMP;
                detected.maxScale = 0;
                detected.signed = false;

                if (cellValue != null) {
                    detected.maxSize = Math.max(detected.maxSize, cellValue.length());
                    detected.maxBeforeDecimalPoint = 0;
                    detected.maxPrecision = 0;
                }

                break;

            case INTEGER:
            case DOUBLE:
            case VARCHAR:
                enterVarChar(detected, cellValue);

                break;

            default:
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "Unexpected column type: " + previousColumnType);


        }
    }

    private static void enterVarChar(DetectionContext detected, String cellValue) {

        final SQLDataType previousColumnType = detected.columnType;
        detected.columnType = SQLDataType.VARCHAR;

        if (previousColumnType == SQLDataType.INTEGER) {

            if (cellValue != null) {
                detected.maxSize = Math.max(detected.maxSize, cellValue.length());
                detected.maxScale = Math.max(detected.maxScale, cellValue.length());

                detected.maxBeforeDecimalPoint = detected.maxSize;
            }

        } else {

            if (cellValue != null) {
                detected.maxSize = Math.max(detected.maxSize, cellValue.length());
                detected.maxScale = Math.max(detected.maxScale, detected.maxSize);
                detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, detected.maxSize);
            }
        }


        detected.signed = false;
        detected.maxPrecision = 0;
        detected.maxColumnDisplaySize = Math.max(detected.maxColumnDisplaySize, detected.maxSize);
    }

}
