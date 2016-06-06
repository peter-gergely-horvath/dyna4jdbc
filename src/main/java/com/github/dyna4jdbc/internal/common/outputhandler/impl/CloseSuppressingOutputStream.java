package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.IOException;
import java.io.OutputStream;

public final class CloseSuppressingOutputStream extends OutputStream {

    private final OutputStream delegate;

    public CloseSuppressingOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        // close is suppressed
    }
}
