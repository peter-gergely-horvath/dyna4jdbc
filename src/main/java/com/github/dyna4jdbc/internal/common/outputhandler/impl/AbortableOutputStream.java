package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import java.io.IOException;
import java.io.OutputStream;

import com.github.dyna4jdbc.internal.ExecutionAbortedError;

public class AbortableOutputStream extends OutputStream {

    private final OutputStream delegate;
    private volatile boolean aborted = false;

    public AbortableOutputStream(OutputStream delegate) {
        this.delegate = delegate;
    }

    @Override
    public final void write(int b) throws IOException {
        checkNotAborted();
        
        delegate.write(b);
    }
    
    public final void abort() {
        checkNotAborted();
        
        aborted = true;
    }

    private void checkNotAborted() {
        if (aborted) {
            throw new ExecutionAbortedError(this + " is already aborted");
        }
    }

}
