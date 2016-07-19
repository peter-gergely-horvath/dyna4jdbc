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

package com.github.dyna4jdbc.internal.config;

import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * An immutable container for configuration attributes supplied
 * by the user through either the JDBC connection URL or the
 * additional properties. </p>
 * 
 * 
 * @author Peter G. Horvath
 *
 * @see com.github.dyna4jdbc.DynaDriver#connect(String, java.util.Properties)
 * @see com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory
 */
public interface Configuration {

    char getCellSeparator();
    boolean getSkipFirstLine();
    boolean getPreferMultipleResultSets();
    String getConversionCharset();
    Pattern getEndOfDataPattern();
    long getExternalCallQuietPeriodThresholdMs();
    List<String> getClasspath();
}
