package com.github.dyna4jdbc.internal.common.util.io;

public final class DelegatingAutoCloseable implements AutoCloseable {

    private final AutoCloseable delegate;

    public DelegatingAutoCloseable(AutoCloseable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() throws Exception {
        delegate.close();
    }
}
