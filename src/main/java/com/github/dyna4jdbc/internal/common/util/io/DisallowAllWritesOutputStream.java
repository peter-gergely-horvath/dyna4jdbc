package com.github.dyna4jdbc.internal.common.util.io;

import com.github.dyna4jdbc.internal.OutputDisabledError;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Peter Horvath
 */
public final class DisallowAllWritesOutputStream extends OutputStream {

    private final String message;

    public DisallowAllWritesOutputStream(String message) {
        this.message = message;
    }

    @Override
    public void write(int b) throws IOException {
        throw new OutputDisabledError(message);
    }
}
