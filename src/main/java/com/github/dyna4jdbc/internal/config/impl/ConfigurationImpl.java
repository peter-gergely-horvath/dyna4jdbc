package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

import java.util.regex.Pattern;

class ConfigurationImpl implements Configuration {

    private char cellSeparator;
    private boolean skipFirstLine;
    private boolean preferMultipleResultSets;
    private String conversionCharset;
    private Pattern endOfDataPattern;

    @Override
    public char getCellSeparator() {
        return this.cellSeparator;
    }

    void setCellSeparator(char c) {
        this.cellSeparator = c;
    }

    @Override
    public boolean getSkipFirstLine() {
        return this.skipFirstLine;
    }

    public void setSkipFirstLine(boolean skipFirstLine) {
        this.skipFirstLine = skipFirstLine;
    }

    @Override
    public boolean getPreferMultipleResultSets() {
        return preferMultipleResultSets;
    }

    @Override
    public Pattern getEndOfDataPattern() {
        return endOfDataPattern;
    }

    public void setConversionCharset(String conversionCharset) {
        this.conversionCharset = conversionCharset;
    }

    public String getConversionCharset() {
        return conversionCharset;
    }

    public void setPreferMultipleResultSets(boolean preferMultipleResultSets) {
        this.preferMultipleResultSets = preferMultipleResultSets;
    }

    public void setEndOfDataPattern(Pattern endOfDataPattern) {
        this.endOfDataPattern = endOfDataPattern;
    }

}
