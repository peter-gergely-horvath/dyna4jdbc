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

/**
 * Thrown to indicate that a method has been passed an illegal or
 * inappropriate configuration value.
 */
public class InvalidConfigurationValueException extends MisconfigurationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an <code>InvalidConfigurationValueException</code> with the
     * specified detail message.
     *
     * @param   message   the detail message.
     */
    public InvalidConfigurationValueException(String message) {
        super(message);
    }

    /**
     * Constructs an <code>InvalidConfigurationValueException</code> with the String formatted with
     * arguments as detail message.
     *
     * @param format the string to format to generate the detail message string
     * @param args argument to be used when formatting the detail message string
     *
     * @return a <code>InvalidConfigurationValueException</code> with detail message built from the parameters
     */
    public static InvalidConfigurationValueException forMessage(String format, Object... args) {
        return new InvalidConfigurationValueException(String.format(format, args));
    }

}
