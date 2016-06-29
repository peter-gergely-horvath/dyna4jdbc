package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.IOException;

/**
 * @author Peter Horvath
 */
public final class BoundedDataTableWriter extends DataTableWriter {

    private final int maxRows;
    private int currentRow = 0;

    BoundedDataTableWriter(Configuration configuration, int maxRows) {
        super(configuration);
        this.maxRows = maxRows;
    }

    protected void nextRow() {
        super.nextRow();

        ++currentRow;
    }

    @Override
    public void write(int b) throws IOException {

        if (currentRow < maxRows) {
            super.write(b);
        }
        /*
        Otherwise, simply swallow the write request: once
        currentRow reaches maxRows, we no longer even convert
        the byte values to strings. */
    }
}
