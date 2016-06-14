package com.github.dyna4jdbc.internal.common.util.io;

import java.io.IOException;
import java.io.OutputStream;

import com.github.dyna4jdbc.internal.AbortedError;

public final class AbortableOutputStream extends OutputStream {

    private final OutputStream delegate;
    private volatile boolean aborted = false;

    public AbortableOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    /**
     * <p>
     * Requests this {@code AbortableOutputStream} to throw
     * {@code AbortedError} on any kind of future operation
     * <b>EXCEPT</b> on {@code close()}.</p>
     *
     * <p>
     * This method is thread-safe: the effect of an {@code abort()}
     * request will immediately be visible from all other threads.
     * </p>
     *
     * @throws IllegalStateException when {@code abort()} has already been called previously.
     */
    public void abort() throws IllegalStateException {
        if (aborted) {
            throw new IllegalStateException("Aborted already");
        }

        aborted = true;
    }

    @Override
    public void write(int b) throws IOException {
        checkNotAborted();

        delegate.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        checkNotAborted();

        delegate.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        checkNotAborted();

        delegate.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        checkNotAborted();

        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        // NOTE: we do NOT "checkNotAborted();"
        
        delegate.close();
    }

    private void checkNotAborted() {
        if (aborted) {
            throw new AbortedError(this + " is aborted");
        }
    }

}
