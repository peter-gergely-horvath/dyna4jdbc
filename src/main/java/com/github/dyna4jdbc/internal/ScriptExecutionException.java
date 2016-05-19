package com.github.dyna4jdbc.internal;

public class ScriptExecutionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ScriptExecutionException(Throwable cause) {
        super(cause);
    }

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

