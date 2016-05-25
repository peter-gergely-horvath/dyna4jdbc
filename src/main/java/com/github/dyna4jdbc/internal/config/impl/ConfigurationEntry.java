package com.github.dyna4jdbc.internal.config.impl;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.DriverPropertyInfo;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

enum ConfigurationEntry {

    CELL_SEPARATOR("cellSeparator", "\t", "The character used as separator character. Default is TAB (\\t).") {
        @Override
        void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException {
            if (value == null || value.length() != 1) {
                throw MisconfigurationException.forMessage(
                        "A singe character is expected for %s, but was: '%s'",
                        this.key, value);
            }

            char charAtZero = value.charAt(0);

            config.setCellSeparator(charAtZero);
        }
    },
    SKIP_FIRST_RESULT_LINE("skipFirstLine", "false",
            "If set to true, the first output line is omitted from the result. Default is false.") {
        @Override
        void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException {

            if (value == null || "".equals(value) || "".equals(value.trim())) {
                throw MisconfigurationException
                        .forMessage("Value for %s cannot be null/whitespace only, but was '%s'",
                                this.key, value);
            }

            switch (value) {
                case "true":
                    config.setSkipFirstLine(true);
                    break;

                case "false":
                    config.setSkipFirstLine(false);
                    break;

                default:
                    throw MisconfigurationException
                            .forMessage("Value for %s must either be 'true' or 'false', but was '%s'",
                                    this.key, value);
            }
        }

        @Override
        public DriverPropertyInfo getDriverPropertyInfo() {
            DriverPropertyInfo propertyInfo = super.getDriverPropertyInfo();
            propertyInfo.choices = new String[]{"true", "false"};
            return propertyInfo;
        }
    },
    PREFER_MULTIPLE_RESULT_SETS("preferMultipleResultSets", "false",
            "If set to true, output with different column count "
                    + "will be considered as a new result set. Default is false.") {
        @Override
        void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException {

            if (value == null || "".equals(value) || "".equals(value.trim())) {
                throw MisconfigurationException
                        .forMessage("Value for %s cannot be null/whitespace only, but was '%s'",
                                this.key, value);
            }

            switch (value) {
                case "true":
                    config.setPreferMultipleResultSets(true);
                    break;

                case "false":
                    config.setPreferMultipleResultSets(false);
                    break;

                default:
                    throw MisconfigurationException
                            .forMessage("Value for %s must either be 'true' or 'false', but was '%s'",
                                    this.key, value);
            }
        }

        @Override
        public DriverPropertyInfo getDriverPropertyInfo() {
            DriverPropertyInfo propertyInfo = super.getDriverPropertyInfo();
            propertyInfo.choices = new String[]{"true", "false"};
            return propertyInfo;
        }
    },
    CHARSET("charset", "UTF-8", "The charset used during character conversion. Default is UTF-8.") {
        @Override
        void setConfiguration(ConfigurationImpl config, String charset) throws MisconfigurationException {
            if (charset == null) {
                throw MisconfigurationException.forMessage("charset cannot be null");
            }

            try {
                if (!Charset.isSupported(charset)) {
                    throw MisconfigurationException.forMessage("Charset is not supported: '%s'", charset);
                }

            } catch (IllegalCharsetNameException e) {
                throw MisconfigurationException.forMessage("Charset is illegal: '%s'", charset);
            }

            config.setConversionCharset(charset);
        }
    };

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    private final String key;
    private final String defaultValue;
    private final String description;

    ConfigurationEntry(String key, String defaultValue, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
    }

    abstract void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException;

    public DriverPropertyInfo getDriverPropertyInfo() {
        DriverPropertyInfo driverPropertyInfo = new DriverPropertyInfo(this.key, defaultValue);
        driverPropertyInfo.description = this.description;
        return driverPropertyInfo;
    }
}
