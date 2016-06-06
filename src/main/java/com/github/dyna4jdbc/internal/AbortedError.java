package com.github.dyna4jdbc.internal;

public class AbortedError extends Error {

    private static final long serialVersionUID = 1L;

    public AbortedError(String msg) {
        super(msg);
    }

}
