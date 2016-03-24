package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public abstract class CursorCellWriterOutputStream extends OutputStream {

    private static final int LF = 0xD;
    private static final int CR = 0xA;

    private int lastByte = -1;


    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

	private char cellSeparator;


    public CursorCellWriterOutputStream(char cellSeparator) {
		this.cellSeparator = cellSeparator;
	}

	@Override
    public void write(int thisByte) throws IOException {

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
        String cellContent = byteArrayOutputStream.toString();

        writeCellValue(cellContent);

        byteArrayOutputStream = new ByteArrayOutputStream();
    }



    public void close() throws IOException {
        try {
            if(byteArrayOutputStream.size() > 0) {
                flushBufferToCell();
            }
        } finally {
            byteArrayOutputStream = null;
        }
    }

    private void checkNotClosed() {
        if (byteArrayOutputStream == null) throw new IllegalStateException(this + " is closed already!");
    }
}

