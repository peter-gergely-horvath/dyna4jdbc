package com.github.dyna4jdbc.internal.common.typeconverter;

public class TypeConversionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TypeConversionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TypeConversionException(String message) {
		super(message);
	}
	
	public TypeConversionException(Throwable cause) {
		super(cause);
	}

	public static TypeConversionException forMessage(String format, Object... args) {
		return new TypeConversionException(String.format(format, args));
	}


}
