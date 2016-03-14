package com.github.dyna4jdbc.internal.scriptengine.outputhandler.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public abstract class CursorCellWriterOutputStream extends OutputStream {

    private static final int TAB = 0x9;

    private static final int LF = 0xD;
    private static final int CR = 0xA;

    private int lastByte = -1;


    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


    @Override
    public void write(int thisByte) throws IOException {

        checkNotClosed();

        if (!((lastByte == LF || lastByte == CR) &&
                (thisByte == LF || thisByte == CR))) {

            switch (thisByte) {
                case LF:
                case CR:

                    flushBufferToCell();

                    nextRow();

                    break;

                case TAB:

                    flushBufferToCell();

                    nextCell();

                    break;

                default:
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

