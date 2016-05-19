package com.github.dyna4jdbc.internal.common.typeconverter;

import com.github.dyna4jdbc.internal.common.typeconverter.impl.SQLDataType;

public interface ColumnMetadata {
    
	enum Nullability { NOT_NULLABLE, NULLABLE, UNKNOWN }
	
	boolean isTakesFirstRowValue();
	
	boolean isCurrency();

	Nullability getNullability();

    boolean isSigned();

    int getColumnDisplaySize();

    String getColumnLabel();

    String getColumnName();

    int getPrecision();

    int getScale();

    SQLDataType getColumnType();

    String getColumnTypeName();

    Class<?> getColumnClass();
    
	String getFormatString();
}
