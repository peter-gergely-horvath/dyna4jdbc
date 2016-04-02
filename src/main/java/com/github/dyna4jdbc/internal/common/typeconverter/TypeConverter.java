package com.github.dyna4jdbc.internal.common.typeconverter;

public interface TypeConverter<T> {
	
	T convert(String input) throws TypeConversionException;
}
