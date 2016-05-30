package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.util.Properties;
import java.util.StringTokenizer;

public final class ConfigurationStringParser {

    private static final ConfigurationStringParser INSTANCE = new ConfigurationStringParser();


    public static ConfigurationStringParser getInstance() {
        return INSTANCE;
    }

    private ConfigurationStringParser() {

    }

    public Properties parseStringToProperties(String configurationString) throws MisconfigurationException {
        Properties properties = new Properties();

        if (configurationString != null) {
            StringTokenizer st = new StringTokenizer(configurationString, ";");
            while (st.hasMoreTokens()) {
                String keyValuePair = st.nextToken();
                String[] keyAndValue = keyValuePair.split("=", 2);

                if (keyAndValue.length == 1) {
                    throw MisconfigurationException.forMessage(
                            "Configuration string '%s' cannot be interpreted as '=' "
                                    + "character separated key-value pairs.",
                            configurationString);
                }


                String key = keyAndValue[0];
                String value = keyAndValue[1];

                if (properties.containsKey(key)) {
                    /* configuration string contains duplicated entries;
                     * e.g. "foo=x;foo=y"
                     * Fail-fast in such cases by throwing exception
                     */
                    throw MisconfigurationException.forMessage(
                            "Configuration string contains duplicated key '%s'.",
                            key);
                }

                properties.setProperty(key, value);
            }
        }
        return properties;
    }
}
