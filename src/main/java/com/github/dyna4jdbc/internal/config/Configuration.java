package com.github.dyna4jdbc.internal.config;

public interface Configuration {

	char getCellSeparator();
	boolean getSkipFirstLine();
	boolean getPreferMultipleResultSets();
	String getConversionCharset();
}
