package com.github.dyna4jdbc.internal.config;

public class MisconfigurationException extends Exception {

    private static final long serialVersionUID = 1L;

    public MisconfigurationException(String message) {
        super(message);
    }

    public static MisconfigurationException forMessage(String format, Object... args) {
        return new MisconfigurationException(String.format(format, args));
    }

}
