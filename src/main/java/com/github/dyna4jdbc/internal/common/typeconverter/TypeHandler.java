package com.github.dyna4jdbc.internal.common.typeconverter;


public interface TypeHandler {
    boolean isCurrency();

    int isNullable();

    boolean isSigned();

    int getColumnDisplaySize();

    String getColumnLabel();

    String getColumnName();

    int getPrecision();

    int getScale();

    int getColumnType();

    String getColumnTypeName();

    Class<?> getColumnClass();
}
