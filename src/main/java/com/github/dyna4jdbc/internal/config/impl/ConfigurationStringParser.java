/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
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
