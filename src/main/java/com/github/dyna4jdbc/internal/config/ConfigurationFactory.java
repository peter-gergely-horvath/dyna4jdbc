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

 
package com.github.dyna4jdbc.internal.config;

import java.util.Properties;

/**
 * A Factory, which can construct a {@link Configuration} from a configuration string
 * (a postfix applied to the JDBC URL), and {@code Properties} passed to the driver
 *
 * @see com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory
 */
public interface ConfigurationFactory {

    /**
     * Constructs a {@link Configuration} from the arguments
     *
     * @param config the configuration string added as a postfix to the JDBC URL
     * @param props the {@code Properties} as passed to the driver
     *
     * @return a {@link Configuration} constructed from the arguments
     *
     * @throws MisconfigurationException if any of the arguments contain invalid configuration settings
     */
    Configuration newConfigurationFromParameters(String config, Properties props) throws MisconfigurationException;
}
