package com.github.dyna4jdbc.internal.config;

import java.util.regex.Pattern;

public interface Configuration {

    char getCellSeparator();
    boolean getSkipFirstLine();
    boolean getPreferMultipleResultSets();
    String getConversionCharset();
    Pattern getEndOfDataPattern();

}
