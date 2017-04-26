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


import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnMetadata;

import java.util.regex.Matcher;

final class TypeDetector {

    interface DetectionResult {
        SQLDataType getColumnType();

        int getMaxSize();

        int getMaxPrecision();

        int getMaxScale();

        int getMaxBeforeDecimalPoint();

        ColumnMetadata.Nullability getNullability();

        boolean isSigned();

        boolean isCurrency();

        int getMaxColumnDisplaySize();
    }

    static class DetectionContext implements DetectionResult {

        private SQLDataType columnType = SQLDataType.OTHER;
        private int maxSize = 0;
        private int maxPrecision = 0;
        private int maxScale = 0;
        private int maxBeforeDecimalPoint = 0;
        private ColumnMetadata.Nullability nullability = ColumnMetadata.Nullability.NOT_NULLABLE;
        private boolean signed = false;
        private boolean currency = false;
        private int maxColumnDisplaySize = 0;

        @Override
        public SQLDataType getColumnType() {
            return columnType;
        }

        @Override
        public int getMaxSize() {
            return maxSize;
        }

        @Override
        public int getMaxPrecision() {
            return maxPrecision;
        }

        @Override
        public int getMaxScale() {
            return maxScale;
        }

        @Override
        public int getMaxBeforeDecimalPoint() {
            return maxBeforeDecimalPoint;
        }

        @Override
        public ColumnMetadata.Nullability getNullability() {
            return nullability;
        }

        @Override
        public boolean isSigned() {
            return signed;
        }

        @Override
        public boolean isCurrency() {
            return currency;
        }

        @Override
        public int getMaxColumnDisplaySize() {
            return maxColumnDisplaySize;
        }
    }

    private TypeDetector() {
        // no external instances
    }

    static DetectionResult detectColumnType(Iterable<String> cellValues) {
        DetectionContext detected = new DetectionContext();

        for (String cellValue : cellValues) {

            detectMetadataByInspectingCellValue(detected, cellValue);
        }

        if (detected.columnType == SQLDataType.OTHER) {
            detected.columnType = SQLDataType.VARCHAR;
        }

        if (detected.maxPrecision == 0) {
            detected.maxColumnDisplaySize = detected.maxSize;
        } else {
            final int decimalPointLength = 1;

            detected.maxColumnDisplaySize =
                    detected.maxBeforeDecimalPoint + decimalPointLength + detected.maxPrecision;

            detected.maxScale = Math.max(detected.maxScale,
                    detected.maxBeforeDecimalPoint + detected.maxPrecision);
        }
        return detected;
    }


    private static void detectMetadataByInspectingCellValue(DetectionContext detectionContext, String cellValue) {

        if (cellValue == null) {
            detectionContext.nullability = ColumnMetadata.Nullability.NULLABLE;
        }

        switch (detectionContext.columnType) {
            case OTHER:
                leaveOther(detectionContext, cellValue);
                break;

            case INTEGER:
                leaveInteger(detectionContext, cellValue);
                break;

            case DOUBLE:
                leaveDouble(detectionContext, cellValue);
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

        // transition VarChar --> VarChar
        enterVarChar(detectionContext, cellValue);
    }

    private static void leaveTimestamp(DetectionContext detectionContext, String cellValue) {
        if (isPlausibleConversion(SQLDataType.TIMESTAMP, cellValue)) {

            // transition Timestamp --> Timestamp
            enterTimestamp(detectionContext, cellValue);

        } else {

            // transition Timestamp --> VarChar
            enterVarChar(detectionContext, cellValue);

        }
    }

    private static void leaveDouble(DetectionContext detectionContext, String cellValue) {
        if (isPlausibleConversion(SQLDataType.DOUBLE, cellValue)
                || isPlausibleConversion(SQLDataType.INTEGER, cellValue)) {

            // transition Double --> Double
            enterDouble(detectionContext, cellValue);
        } else {

            // transition Double --> VarChar
            enterVarChar(detectionContext, cellValue);
        }
    }

    private static void leaveOther(DetectionContext detectionContext, String cellValue) {

        if (cellValue == null) {

            // NO-OP: transition Other --> Other
            return;

        } else if (isPlausibleConversion(SQLDataType.INTEGER, cellValue)) {

            // transition Other --> Integer
            enterInteger(detectionContext, cellValue);

        } else if (isPlausibleConversion(SQLDataType.DOUBLE, cellValue)) {

            // transition Other --> Double
            enterDouble(detectionContext, cellValue);

        } else if (isPlausibleConversion(SQLDataType.TIMESTAMP, cellValue)) {

            // transition Other --> Timestamp
            enterTimestamp(detectionContext, cellValue);

        } else {

            // transition Other --> VarChar
            enterVarChar(detectionContext, cellValue);
        }
    }

    private static void leaveInteger(DetectionContext detectionContext, String cellValue) {
        if (isPlausibleConversion(SQLDataType.INTEGER, cellValue)) {

            // transition Integer --> Integer
            enterInteger(detectionContext, cellValue);

        } else if (isPlausibleConversion(SQLDataType.DOUBLE, cellValue)) {

            // transition Integer --> Double
            enterDouble(detectionContext, cellValue);
        } else {

            // transition Integer --> VarChar
            enterVarChar(detectionContext, cellValue);
        }
    }

    private static void enterInteger(DetectionContext detected, String cellValue) {
        detected.columnType = SQLDataType.INTEGER;
        detected.signed = true;

        if (cellValue != null) {
            final int scale = getScale(SQLDataType.INTEGER, cellValue);

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

            final int scale = getScale(SQLDataType.DOUBLE, cellValue);
            final int precision = getPrecision(SQLDataType.DOUBLE, cellValue);

            detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, scale - precision);
            detected.maxPrecision = Math.max(detected.maxPrecision, precision);

            detected.maxSize = Math.max(detected.maxSize, cellValue.length());

            detected.maxScale = Math.max(detected.maxScale, scale);
        }
    }

    private static void enterTimestamp(DetectionContext detected, String cellValue) {

        detected.columnType = SQLDataType.TIMESTAMP;
        detected.maxScale = 0;
        detected.maxBeforeDecimalPoint = 0;
        detected.maxPrecision = 0;
        detected.signed = false;

        if (cellValue != null) {
            detected.maxSize = Math.max(detected.maxSize, cellValue.length());
        }
    }

    private static void enterVarChar(DetectionContext detected, String cellValue) {

        final SQLDataType previousColumnType = detected.columnType;
        detected.columnType = SQLDataType.VARCHAR;

        switch (previousColumnType) {
            case INTEGER:
                detected.maxBeforeDecimalPoint = detected.maxSize;

                break;


            case DOUBLE:
                detected.maxScale = detected.maxSize;
                detected.maxColumnDisplaySize = detected.maxSize;

                break;

            default:
                break; // NO-OP for other previous types
        }

        if (cellValue != null) {
            detected.maxSize = Math.max(detected.maxSize, cellValue.length());
            detected.maxScale = Math.max(detected.maxScale, detected.maxSize);
            detected.maxBeforeDecimalPoint = Math.max(detected.maxBeforeDecimalPoint, detected.maxSize);
        }

        detected.signed = false;
        detected.maxPrecision = 0;
        detected.maxColumnDisplaySize = Math.max(detected.maxColumnDisplaySize, detected.maxSize);
    }

    private static boolean isPlausibleConversion(SQLDataType dataType, String value) {
        if (dataType == SQLDataType.VARCHAR) {
            return true;
        }

        if (dataType.acceptedPattern == null) {
            return false;
        }

        if (value == null) {
            return true;
        }

        return dataType.acceptedPattern.matcher(value).matches();
    }

    private static int getScale(SQLDataType dataType, String value) {
        if (value == null) {
            return 0;
        }

        if (dataType.acceptedPattern == null) {
            return value.length();
        }

        Matcher matcher = dataType.acceptedPattern.matcher(value);
        if (matcher.matches()) {
            final int groupCount = matcher.groupCount();
            if (groupCount == 0) {
                String fullMatchedString = matcher.group(0);
                if (fullMatchedString != null) {
                    return fullMatchedString.length();
                } else {
                    return 0;
                }
            } else {
                int aggregatedLength = 0;
                for (int i = 1; i <= groupCount; i++) {
                    String group = matcher.group(i);
                    if (group != null) {
                        aggregatedLength += group.length();
                    }
                }
                return aggregatedLength;
            }
        }

        return 0;
    }

    private static int getPrecision(SQLDataType dataType, String value) {
        if (value == null) {
            return 0;
        }

        if (dataType.acceptedPattern == null) {
            return 0;
        }

        Matcher matcher = dataType.acceptedPattern.matcher(value);
        if (matcher.matches()) {
            String precisionPart = matcher.group("precision");
            if (precisionPart != null) {
                return precisionPart.length();
            }
        }
        return 0;
    }


}
