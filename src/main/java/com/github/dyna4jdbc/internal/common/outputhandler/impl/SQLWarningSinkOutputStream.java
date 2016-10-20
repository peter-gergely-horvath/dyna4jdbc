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

 
package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLWarning;


class SQLWarningSinkOutputStream extends OutputStream {

    private static final int LF = 0xD;
    private static final int CR = 0xA;

    private int lastByte = -1;
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


    private final SQLWarningSink sqlWarningSink;
    private final String charsetName;

    SQLWarningSinkOutputStream(Configuration configuration, SQLWarningSink warningSink) {
        this.charsetName = configuration.getConversionCharset();
        this.sqlWarningSink = warningSink;
    }

    @Override
    public final void write(int thisByte) throws IOException {

        checkNotClosed();

        if (!((lastByte == LF || lastByte == CR)
                && (thisByte == LF || thisByte == CR))) {

            if (thisByte == LF || thisByte == CR) {
                flushBufferToSQLWarning();

            }  else {
                byteArrayOutputStream.write(thisByte);
            }
        }

        lastByte = thisByte;
    }

    private void flushBufferToSQLWarning() {

        try {
            String message = byteArrayOutputStream.toString(charsetName);

            appendAsSQLWarning(message);

            byteArrayOutputStream = new ByteArrayOutputStream();

        } catch (UnsupportedEncodingException e) {
            // should not happen: we test the configuration before applying it
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(e,
                    "The requested charsetName is not supported: " + charsetName);
        }
    }

    private void appendAsSQLWarning(String value) {

        SQLWarning sqlwarning = new SQLWarning(value);

        sqlWarningSink.onSQLWarning(sqlwarning);
    };


    public final void close() throws IOException {

        checkNotClosed();

        try {
            if (byteArrayOutputStream.size() > 0) {
                flushBufferToSQLWarning();
            }
        } finally {
            byteArrayOutputStream = null;
        }
    }

    private void checkNotClosed() {
        if (byteArrayOutputStream == null) {
            throw new IllegalStateException(this + " is closed already!");
        }
    }
}

