package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.JDBCError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public abstract class CursorCellWriterOutputStream extends OutputStream {

    private static final int LF = 0xD;
    private static final int CR = 0xA;

    private int lastByte = -1;


    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	private final char cellSeparator;
    private final String charsetName;


    public CursorCellWriterOutputStream(char cellSeparator, String charsetName) {
		this.cellSeparator = cellSeparator;
        this.charsetName = charsetName;
	}

	@Override
    public final void write(int thisByte) throws IOException {

        checkNotClosed();

        if (!((lastByte == LF || lastByte == CR) &&
                (thisByte == LF || thisByte == CR))) {


        	if(thisByte == LF || thisByte == CR  ) {
                flushBufferToCell();

                nextRow();
                
        	} else if(thisByte == cellSeparator) {
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



    public void close() throws IOException {

        checkNotClosed();

        try {
            if(byteArrayOutputStream.size() > 0) {
                flushBufferToCell();
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

