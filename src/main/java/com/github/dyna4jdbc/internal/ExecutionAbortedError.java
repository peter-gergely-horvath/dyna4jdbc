package com.github.dyna4jdbc.internal;

public class ExecutionAbortedError extends Error {

    private static final long serialVersionUID = 1L;

    public ExecutionAbortedError(String msg) {
        super(msg);
    }

}
