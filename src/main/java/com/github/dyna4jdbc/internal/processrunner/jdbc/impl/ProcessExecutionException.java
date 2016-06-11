package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

class ProcessExecutionException extends Exception {

    private static final long serialVersionUID = 1L;

    ProcessExecutionException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    ProcessExecutionException(Throwable cause) {
        super(cause);
    }
}
