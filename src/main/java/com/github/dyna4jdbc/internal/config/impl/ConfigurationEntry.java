/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.config.impl;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.sql.DriverPropertyInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.github.dyna4jdbc.internal.config.InvalidConfigurationValueException;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

public enum ConfigurationEntry {

    INIT_SCRIPT("initScript", "", 
            "Path of an initialization script, which will be executed "
            + "when the connection is estabilished. Default is empty ") {
        @Override
        void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException {
            if (value != null && value.trim().length() != 0) {
                
                config.setInitScriptPath(value);
            }
        }
    },
    CELL_SEPARATOR("cellSeparator", "\t", "The character used as separator character. Default is TAB (\\t).") {
        @Override
        void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException {
            if (value == null || value.length() != 1) {
                throw InvalidConfigurationValueException.forMessage(
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
                throw InvalidConfigurationValueException
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
                    throw InvalidConfigurationValueException
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
                throw InvalidConfigurationValueException
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
                    throw InvalidConfigurationValueException
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
                    throw InvalidConfigurationValueException.forMessage("Charset is not supported: '%s'", charset);
                }

            } catch (IllegalCharsetNameException e) {
                throw InvalidConfigurationValueException.forMessage("Charset is illegal: '%s'", charset);
            }

            config.setConversionCharset(charset);
        }
    },
    CLASSPATH("classpath", "", "The classpath to use when loading the ScriptEngine. "
            + "Default is empty (expect the application to provide the library).") {

                @Override
                void setConfiguration(ConfigurationImpl config, String value) throws MisconfigurationException {
                    List<String> classpathList;
                    if (value == null || "".equals(value.trim())) {
                        classpathList = Collections.emptyList();
                    } else {
                        classpathList = Arrays.asList(value.split(","));
                    }
                    config.setClasspath(classpathList);
                }
    },
    ENF_OF_DATA_REGEX("endOfDataRegex", null,
                                "The regular expression to detect end of data for interactive console applications.") {
        @Override
        void setConfiguration(ConfigurationImpl config, String endOfDataRegex) throws MisconfigurationException {

            if (endOfDataRegex != null) {

                try {

                    Pattern pattern = Pattern.compile(endOfDataRegex);

                    config.setEndOfDataPattern(pattern);

                } catch (PatternSyntaxException pse) {
                    throw InvalidConfigurationValueException.forMessage(
                            "The value for %s cannot be interpreted as a Java Regular Expression: '%s'",
                            this.key, endOfDataRegex);
                }
            }
        }
    },
    EXTERNAL_COMMAND_NO_OUTPUT_EXPIRATION_INTERVAL_MS("endOfDataTimeoutMs", "3000",
            "A timeout in milliseconds, after which, if an external program does not print anything more, "
                    + "its execution is considered to be finished, even if the process continues to run. "
                    + "Default is 3000") {
        @Override
        void setConfiguration(ConfigurationImpl config, String value)
                throws MisconfigurationException {


            try {

                Long threshold = Long.valueOf(value);

                if (threshold <= 0) {
                    throw InvalidConfigurationValueException.forMessage(
                            "The value for %s must be a positive value", this.key);
                }

                config.setExternalCallQuietPeriodThresholdMs(threshold);

            } catch (NumberFormatException nfe) {

                throw InvalidConfigurationValueException.forMessage(
                        "The value for %s cannot be interpreted as a number: '%s'",
                        this.key, value);

            }
        }
    };

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    //CHECKSTYLE.OFF: VisibilityModifier
    protected final String key;
    //CHECKSTYLE.ON: VisibilityModifier
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
