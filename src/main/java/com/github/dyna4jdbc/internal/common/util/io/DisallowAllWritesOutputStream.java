package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.OutputDisabledError;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Peter Horvath
 */
final class DisallowAllWritesOutputStream extends OutputStream {

    private final String message;

    DisallowAllWritesOutputStream(String message) {
        this.message = message;
    }


    @Override
    public void write(int b) throws IOException {
        throw new OutputDisabledError(message);
    }
}
