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

 
package com.github.dyna4jdbc.internal.common.util.io;

import java.io.IOException;
import java.io.OutputStream;

import com.github.dyna4jdbc.internal.AbortedError;

public final class AbortableOutputStream extends OutputStream {

    private final OutputStream delegate;
    private final AbortHandler abortHandler;
    
    public AbortableOutputStream(OutputStream delegate, AbortHandler abortHandler) {
        if (delegate == null) {
            throw new NullPointerException("Argument delegate cannot be null");
        }
        if (abortHandler == null) {
            throw new NullPointerException("Argument abortHandler cannot be null");
        }
        
        this.delegate = delegate;
        this.abortHandler = abortHandler;
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
        checkNotAborted();
        
        delegate.close();
    }

    private void checkNotAborted() {
        if (abortHandler.aborted) {
            throw new AbortedError(this + ": OutputStream is aborted");
        }
    }

    public static final class AbortHandler {
        private volatile boolean aborted = false;

        /**
         * <p>
         * Requests all {@code AbortableOutputStream}s watching
         * this {@code AbortHandler} to throw {@code AbortedError} on
         * any kind of future operation.</p>
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
    }
}
