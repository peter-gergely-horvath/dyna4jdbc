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

    /**
     * Returns the character used to separate column content within the same row (record). For example,
     * if this method returns "{@code \t}", then a row {@code FOO\tBAR } generated by the user script is to be parsed
     * to two cells (columns), containing "{@code FOO}" and "{@code BAR}".
     *
     * @return the character used to separate column content within the same row (record)
     */
    char getCellSeparator();


    /**
     * Returns and indication whether the first output row generated by the user script should be discarded or not.
     * If {@code true}, the first line emitted by the user script should <b>NOT</b> considered as part of the
     * actual result set, and is to be skipped / discarded; if it is {@code false} the first line emitted by the
     * user script is to be processed as any other line.
     *
     * @return {@code true} if the first line emitted by the user script is to be skipped during processing;
     *          {@code false} otherwise
     */
    boolean getSkipFirstLine();

    /**
     * <p>
     * Returns and indication whether multiple result sets are preferred or not.</p>
     * <p>
     * Consider that JDBC API allows a statement to return MULTIPLE {@code ResultSet}s: if this method returns
     * {@code true}, a row with column count different from the previous one should cause a new {@code ResultSet}
     * to be added to the results of the statement with the row. If {@code false} is returned, configuration
     * commands parsing subsystem to consider a row with different row length to be added to the same output
     * {@code ResultSet} and use the column count established from the longest row within the {@code ResultSet}.</p>
     *
     * @return {@code true}, if multiple result sets are preferred, {@code false} otherwise
     */
    boolean getPreferMultipleResultSets();

    /**
     * Returns the name of the Java Charset name to be used during conversion (never {@code null})
     *
     * @return the name of the Java Charset name to be used during conversion (never {@code null})
     */
    String getConversionCharset();

    /**
     * Returns a {@code Pattern} to be used to detect the end of user script generated output
     *      (<b>MIGHT</b> be {@code null})
     *
     * @return a {@code Pattern} to be used to detect the end of user script generated output, or {@code null})
     *      if no such {@code Pattern} is configured
     */
    Pattern getEndOfDataPattern();

    /**
     * Returns a positive number indicating the time duration in milliseconds, after which - if the external
     * process no longer generates output - an external process based user script execution (where the end of
     * the execution cannot be otherwise explicitly detected) is to be considered as finished.
     *
     * @return a positive number indicating the time duration in milliseconds of the "quiet" period,
     *          after which a external process based user script execution is to be considered as finished.
     */
    long getExternalCallQuietPeriodThresholdMs();


    /**
     * Returns a list of additional entries to be added to the classpath before executing any user script.
     * (Applicable to JVM-based user script connections only)
     *
     * @return a list of additional entries to be added to the classpath before executing any user script
     *      (never {@code null})
     */
    List<String> getClasspath();

    /**
     * Returns the path of an initialization script, which is to be automatically executed within the
     * connection, after the connection is established. (<b>MIGHT</b> be {@code null})
     *
     * @return the path of an initialization script, or {@code null} if no such initialization script is configured
     */
    String getInitScriptPath();
}
