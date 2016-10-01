package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

public enum AdditionalColumnConfiguration {
    
    FORMAT("format") {
        @Override
        void setConfiguration(DefaultColumnMetadata metaData, String formatString) throws MisconfigurationException {
            metaData.setFormatString(formatString);
        }
    };
    
    private final String key;
    
    public String getKey() {
        return key;
    }

    AdditionalColumnConfiguration(String key) {
        this.key = key;
    }
    
    abstract void setConfiguration(DefaultColumnMetadata metaData, String value) throws MisconfigurationException;

}
