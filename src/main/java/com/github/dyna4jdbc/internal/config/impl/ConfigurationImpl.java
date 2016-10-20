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

import com.github.dyna4jdbc.internal.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * <p>
 * The implementation of {@link Configuration}, which allows settings the values in a 
 * standard POJO-like fashion. </p>
 * 
 * <p>
 * This class is intentionally made package-local: the world outside of this package
 * should not be able to make any kind of adjustment to the {@code Configuration}
 * object, but should use
 * {@link com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory
 * DefaultConfigurationFactory} to acquire a read-only instance (with no setter
 * methods exposed)</p>
 *  
 * <p>
 * Notices for the implementation: this class shall be a (technically) immutable
 * data container, and nothing more:
 * <ul>
 * <li>This is a POJO class: no logic shall be implemented inside.</li>
 * <li>Beware of mutable data (arrays, {@code List}s, {@code Date} etc):
 *      create a defensive copy.</li>
 * <li>Never expose a mutable data type (arrays, {@code List}s, {@code Date} etc)
 *      directly: return a copy instead.</li>
 * </ul>
 * </p>
 *
 * @author Peter G. Horvath
 */
class ConfigurationImpl implements Configuration {

    private char cellSeparator;
    private boolean skipFirstLine;
    private boolean preferMultipleResultSets;
    private String conversionCharset;
    private Pattern endOfDataPattern;
    private long externalCallQuietPeriodThresholdMs;
    private List<String> classpath;
    private String initScriptPath;

    // public getters
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

    @Override
    public long getExternalCallQuietPeriodThresholdMs() {
        return externalCallQuietPeriodThresholdMs;
    }

    @Override
    public String getConversionCharset() {
        return conversionCharset;
    }

    @Override
    public List<String> getClasspath() {
        /* The returned list is immutable ("unmodifiable") so client code
         * cannot accidentally change the global configuration by changing
         * the contents of the returned list */
        return Collections.unmodifiableList(classpath);
    }

    @Override
    public String getInitScriptPath() {
        return initScriptPath;
    }

    // package-local setters
    void setConversionCharset(String conversionCharset) {
        this.conversionCharset = conversionCharset;
    }

    void setPreferMultipleResultSets(boolean preferMultipleResultSets) {
        this.preferMultipleResultSets = preferMultipleResultSets;
    }

    void setEndOfDataPattern(Pattern endOfDataPattern) {
        this.endOfDataPattern = endOfDataPattern;
    }

    void setExternalCallQuietPeriodThresholdMs(long externalCallQuietPeriodThresholdMs) {
        this.externalCallQuietPeriodThresholdMs = externalCallQuietPeriodThresholdMs;
    }

    void setClasspath(List<String> classpath) {
        // create a defensive copy of the *source* list so that
        // unexpected changes to the original list cannot bite us later on
        this.classpath = new ArrayList<>(classpath);
    }

    public void setinitScriptPath(String initScript) {
        this.initScriptPath = initScript;
    }

}
