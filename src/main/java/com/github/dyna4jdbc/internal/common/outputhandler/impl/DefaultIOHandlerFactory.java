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

 
package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.util.io.SQLWarningSinkOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Peter G. Horvath
 */
public final class DefaultIOHandlerFactory implements IOHandlerFactory {

    private final Configuration configuration;

    private static final AtomicReference<DefaultIOHandlerFactory> INSTANCE = new AtomicReference<>();

    private DefaultIOHandlerFactory(Configuration configuration) {
        validateCharacterSetName(configuration.getConversionCharset());

        this.configuration = configuration;
    }

    private static void validateCharacterSetName(String characterSetName) {
        try {
            if (!Charset.isSupported(characterSetName)) {
                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                        "Unsupported characterSetName: " + characterSetName);
            }
        } catch (IllegalCharsetNameException icne) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    "Illegal characterSetName: " + characterSetName);
        }
    }


    public static DefaultIOHandlerFactory getInstance(Configuration configuration) {

        DefaultIOHandlerFactory current = INSTANCE.get();
        if (current == null 
                || !current.configuration.equals(configuration)) {

            current = new DefaultIOHandlerFactory(configuration);
            INSTANCE.set(current);
        }

        return current;
    }

    @Override
    public PrintWriter newPrintWriter(OutputStream outputStream, boolean autoFlush) {
        Objects.requireNonNull(outputStream, "argument outputStream cannot be null");
        
        return new PrintWriter(newOutputStreamWriter(outputStream), autoFlush);
    }

    @Override
    public OutputStreamWriter newOutputStreamWriter(OutputStream outputStream) {
        Objects.requireNonNull(outputStream, "argument outputStream cannot be null");

        String characterSetName = configuration.getConversionCharset();
        try {
            return new OutputStreamWriter(outputStream, characterSetName);
        } catch (UnsupportedEncodingException e) {
            // should not happen: we check the validity of the characterSetName before!
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(e,
                    "Unsupported characterSetName: " + characterSetName);
        }
    }

    @Override
    public PrintStream newPrintStream(OutputStream outputStream) {
        Objects.requireNonNull(outputStream, "argument outputStream cannot be null");
        
        String characterSetName = configuration.getConversionCharset();
        try {
            return new PrintStream(outputStream, true, characterSetName);
        } catch (UnsupportedEncodingException e) {
            // should not happen: we check the validity of the characterSetName before!
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(e,
                    "Unsupported characterSetName: " + characterSetName);
        }
    }

    @Override
    public BufferedReader newBufferedReader(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "argument inputStream cannot be null");

        String characterSetName = configuration.getConversionCharset();
        try {
            return new BufferedReader(new InputStreamReader(inputStream, characterSetName));
        } catch (UnsupportedEncodingException e) {
            // should not happen: we check the validity of the characterSetName before!
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(e,
                    "Unsupported characterSetName: " + characterSetName);
        }
    }

    @Override
    public OutputStream newWarningSinkOutputStream(SQLWarningSink sqlWarningSink) {
        Objects.requireNonNull(sqlWarningSink, "argument sqlWarningSink cannot be null");

        return new SQLWarningSinkOutputStream(this.configuration, sqlWarningSink);
    }
}
