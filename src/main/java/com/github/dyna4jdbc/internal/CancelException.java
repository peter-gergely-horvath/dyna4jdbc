package com.github.dyna4jdbc.internal;

public class CancelException extends Exception {

    private static final long serialVersionUID = 1L;

    public CancelException(String message) {
        super(message);
    }
    
    public CancelException(Throwable cause) {
        super(cause);
    }

    public CancelException(String message, Throwable cause) {
        super(message, cause);
    }
}

