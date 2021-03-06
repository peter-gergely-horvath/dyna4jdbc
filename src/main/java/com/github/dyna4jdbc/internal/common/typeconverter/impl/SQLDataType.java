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

import java.util.regex.Pattern;

public enum SQLDataType {

    BIT(java.sql.Types.BIT,
            "BIT", java.lang.Boolean.class, false, 
            TypeDetectionRegularExpressionPatterns.ONE_BIT),
    TINYINT(java.sql.Types.TINYINT,
            "TINYINT", java.lang.Short.class, false, 
            TypeDetectionRegularExpressionPatterns.NUMBER_INTEGER),
    SMALLINT(java.sql.Types.SMALLINT,
            "SMALLINT", java.lang.Short.class, false, 
            TypeDetectionRegularExpressionPatterns.NUMBER_INTEGER),
    INTEGER(java.sql.Types.INTEGER,
            "INTEGER", java.lang.Integer.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_INTEGER),
    BIGINT(java.sql.Types.BIGINT,
            "BIGINT", java.lang.Long.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_INTEGER),
    FLOAT(java.sql.Types.FLOAT,
            "FLOAT", java.lang.Float.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_DECIMAL),
    REAL(java.sql.Types.REAL,
            "REAL", java.lang.Float.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_DECIMAL),
    DOUBLE(java.sql.Types.DOUBLE,
            "DOUBLE", java.lang.Double.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_DECIMAL),
    NUMERIC(java.sql.Types.NUMERIC,
            "NUMERIC", java.lang.Double.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_DECIMAL),
    DECIMAL(java.sql.Types.DECIMAL,
            "DECIMAL", java.math.BigDecimal.class, false,
            TypeDetectionRegularExpressionPatterns.NUMBER_DECIMAL),
    CHAR(java.sql.Types.CHAR,
            "CHAR", java.lang.String.class),
    VARCHAR(java.sql.Types.VARCHAR,
            "VARCHAR", java.lang.String.class),
    LONGVARCHAR(java.sql.Types.LONGVARCHAR,
            "LONGVARCHAR", java.lang.String.class),
    DATE(java.sql.Types.DATE,
            "DATE", java.sql.Date.class),
    TIME(java.sql.Types.TIME,
            "TIME", java.sql.Time.class),
    TIMESTAMP(java.sql.Types.TIMESTAMP,
            "TIMESTAMP", java.sql.Timestamp.class, false,
            TypeDetectionRegularExpressionPatterns.TIMESTAMP),
    BINARY(java.sql.Types.BINARY,
            "BINARY", java.lang.Byte.class, true),
    VARBINARY(java.sql.Types.VARBINARY,
            "VARBINARY", java.lang.Byte.class, true),
    LONGVARBINARY(java.sql.Types.LONGVARBINARY,
            "LONGVARBINARY", java.lang.Byte.class, true),
    NULL(java.sql.Types.NULL,
            "NULL", null),
    OTHER(java.sql.Types.OTHER,
            "OTHER", null),
    JAVA_OBJECT(java.sql.Types.JAVA_OBJECT,
            "JAVA_OBJECT", null),
    DISTINCT(java.sql.Types.DISTINCT,
            "DISTINCT", null),
    STRUCT(java.sql.Types.STRUCT,
            "STRUCT", null),
    ARRAY(java.sql.Types.ARRAY,
            "ARRAY", null),
    BLOB(java.sql.Types.BLOB,
            "BLOB", java.lang.Byte.class, true),
    CLOB(java.sql.Types.CLOB,
            "CLOB", java.lang.String.class),
    REF(java.sql.Types.REF,
            "REF", null),
    DATALINK(java.sql.Types.DATALINK,
            "DATALINK", null),
    BOOLEAN(java.sql.Types.BOOLEAN,
            "BOOLEAN", java.lang.Boolean.class),
    ROWID(java.sql.Types.ROWID,
            "ROWID", null),
    NCHAR(java.sql.Types.NCHAR,
            "NCHAR", java.lang.String.class),
    NVARCHAR(java.sql.Types.NVARCHAR,
            "NVARCHAR", java.lang.String.class),
    LONGNVARCHAR(java.sql.Types.LONGNVARCHAR,
            "LONGNVARCHAR", java.lang.String.class),
    NCLOB(java.sql.Types.NCLOB,
            "NCLOB", java.lang.String.class),
    SQLXML(java.sql.Types.SQLXML,
            "SQLXML", java.lang.String.class),
    REF_CURSOR(java.sql.Types.REF_CURSOR,
            "REF_CURSOR", java.lang.String.class),
    TIME_WITH_TIMEZONE(java.sql.Types.TIME_WITH_TIMEZONE,
            "TIME_WITH_TIMEZONE", java.sql.Timestamp.class),
    TIMESTAMP_WITH_TIMEZONE(java.sql.Types.TIMESTAMP_WITH_TIMEZONE,
            "TIMESTAMP_WITH_TIMEZONE", java.sql.Timestamp.class);


    //CHECKSTYLE.OFF: VisibilityModifier
    public final int javaSqlTypesInt;
    public final String name;
    public final Class<?> mappingClass;
    public final boolean isArray;
    final Pattern acceptedPattern;
    //CHECKSTYLE.ON: VisibilityModifier

    SQLDataType(int javaSqlTypesInt, String name, Class<?> mappingClass) {
        this(javaSqlTypesInt, name, mappingClass, false);
    }

    SQLDataType(int javaSqlTypesInt, String name, Class<?> mappingClass, boolean isArray) {
        this(javaSqlTypesInt, name, mappingClass, false, null);
    }

    SQLDataType(int javaSqlTypesInt, String name, Class<?> mappingClass, boolean isArray, Pattern acceptedPattern) {
        this.javaSqlTypesInt = javaSqlTypesInt;
        this.name = name;
        this.mappingClass = mappingClass;
        this.isArray = isArray;
        this.acceptedPattern = acceptedPattern;
    }
}
