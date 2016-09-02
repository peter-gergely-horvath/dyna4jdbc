package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum SQLDataType {

    // TODO: extract methods
    BIT(java.sql.Types.BIT,
            "BIT", java.lang.Boolean.class, false, "^([01])$"),
    TINYINT(java.sql.Types.TINYINT,
            "TINYINT", java.lang.Short.class, false, "^[+-]?((\\d+))$"),
    SMALLINT(java.sql.Types.SMALLINT,
            "SMALLINT", java.lang.Short.class, false, "^[+-]?(\\d+)$"),
    INTEGER(java.sql.Types.INTEGER,
            "INTEGER", java.lang.Integer.class, false, "^[+-]?(\\d+)$") {
        @Override
        int getPrecision(String value) {
            return 0;
        }
    },
    BIGINT(java.sql.Types.BIGINT,
            "BIGINT", java.lang.Long.class, false, "^[+-]?(\\d+)$"),
    FLOAT(java.sql.Types.FLOAT,
            "FLOAT", java.lang.Float.class, false, "^[+-]?(\\d+)\\.(?<precision>\\d+)$"),
    REAL(java.sql.Types.REAL,
            "REAL", java.lang.Float.class, false, "^[+-]?(\\d+)\\.(?<precision>\\d+)$"),
    DOUBLE(java.sql.Types.DOUBLE,
            "DOUBLE", java.lang.Double.class, false, "^[+-]?(\\d+)\\.(?<precision>\\d+)$"),
    NUMERIC(java.sql.Types.NUMERIC,
            "NUMERIC", java.lang.Double.class, false, "^[+-]?(\\d+)\\.(?<precision>\\d+)$"),
    DECIMAL(java.sql.Types.DECIMAL,
            "DECIMAL", java.math.BigDecimal.class, false, "^[+-]?(\\d+)\\.(?<precision>\\d+)$"),
    CHAR(java.sql.Types.CHAR,
            "CHAR", java.lang.String.class),
    VARCHAR(java.sql.Types.VARCHAR,
            "VARCHAR", java.lang.String.class) {
        @Override
        boolean isPlausibleConversion(String value) {
            return true;
        }
    },
    LONGVARCHAR(java.sql.Types.LONGVARCHAR,
            "LONGVARCHAR", java.lang.String.class),
    DATE(java.sql.Types.DATE,
            "DATE", java.sql.Date.class),
    TIME(java.sql.Types.TIME,
            "TIME", java.sql.Time.class),
    TIMESTAMP(java.sql.Types.TIMESTAMP,
            "TIMESTAMP", java.sql.Timestamp.class, false,
            "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?") {
        int getPrecision(String value) {
            return 0;
        }
        int getScale(String value)  {
            return 0;
        }
    },
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
    private final Pattern acceptedPattern;
    //CHECKSTYLE.ON: VisibilityModifier

    SQLDataType(int javaSqlTypesInt, String name, Class<?> mappingClass) {
        this(javaSqlTypesInt, name, mappingClass, false);
    }

    SQLDataType(int javaSqlTypesInt, String name, Class<?> mappingClass, boolean isArray) {
        this(javaSqlTypesInt, name, mappingClass, false, null);
    }

    SQLDataType(int javaSqlTypesInt, String name, Class<?> mappingClass, boolean isArray, String acceptedPatternRegex) {
        this.javaSqlTypesInt = javaSqlTypesInt;
        this.name = name;
        this.mappingClass = mappingClass;
        this.isArray = isArray;
        //CHECKSTYLE.OFF: AvoidInlineConditionals
        this.acceptedPattern = acceptedPatternRegex != null ? Pattern.compile(acceptedPatternRegex) : null;
        //CHECKSTYLE.ON: AvoidInlineConditionals
    }

    boolean isPlausibleConversion(String value) {
        if (this.acceptedPattern == null) {
            return false;
        }

        if (value == null) {
            return true;
        }

        return this.acceptedPattern.matcher(value).matches();
    }

    int getPrecision(String value) {
        if (value == null) {
            return 0;
        }

        if (this.acceptedPattern == null) {
            return 0;
        }

        Matcher matcher = this.acceptedPattern.matcher(value);
        if (matcher.matches()) {
            String precisionPart = matcher.group("precision");
            if (precisionPart != null) {
                return precisionPart.length();
            }
        }
        return 0;
    }

    int getScale(String value) {
        if (value == null) {
            return 0;
        }

        if (this.acceptedPattern == null) {
            return value.length();
        }

        Matcher matcher = this.acceptedPattern.matcher(value);
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

        /*
        TODO: Clean up: required for handling of edge cases like:
        DOUBLE type is used to establish the scale of an integer
        e.g. 1234 does not match double pattern, however can be
        interpreted as a double value. */
        return value.length();
    }

}
