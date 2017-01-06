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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


abstract class CursorCellWriterOutputStream extends OutputStream {

    private static final int LF = 0xD;
    private static final int CR = 0xA;

    private int lastByte = -1;


    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    private final char cellSeparator;
    private final String charsetName;


    CursorCellWriterOutputStream(char cellSeparator, String charsetName) {
        this.cellSeparator = cellSeparator;
        this.charsetName = charsetName;
    }

    @Override
    public void write(int thisByte) throws IOException {

        checkNotClosed();

        if (!((lastByte == LF || lastByte == CR)
                && (thisByte == LF || thisByte == CR))) {


            if (thisByte == LF || thisByte == CR) {
                flushBufferToCell();

                nextRow();

            } else if (thisByte == cellSeparator) {
                flushBufferToCell();

                nextCell();

            } else {
                byteArrayOutputStream.write(thisByte);
            }
        }

        lastByte = thisByte;


    }

    protected abstract void nextCell();

    protected abstract void nextRow();

    protected abstract void writeCellValue(String value);

    private void flushBufferToCell() {

        try {
            String cellContent = byteArrayOutputStream.toString(charsetName);

            writeCellValue(cellContent);

            byteArrayOutputStream = new ByteArrayOutputStream();

        } catch (UnsupportedEncodingException e) {
            // should not happen: we test the configuration before applying it
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(e,
                    "The requested charsetName is not supported: " + charsetName);
        }
    }


    //CHECKSTYLE.OFF: DesignForExtension
    public void close() throws IOException {

        checkNotClosed();

        try {
            if (byteArrayOutputStream.size() > 0) {
                flushBufferToCell();
            }
        } finally {
            byteArrayOutputStream = null;
        }
    }
    //CHECKSTYLE.ON: DesignForExtension

    private void checkNotClosed() {
        if (byteArrayOutputStream == null) {
            throw new IllegalStateException(this + " is closed already!");
        }
    }
}

