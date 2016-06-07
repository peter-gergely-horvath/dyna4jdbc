package com.github.dyna4jdbc.internal.config;

public class DuplicatedKeyInConfigurationException extends MisconfigurationException {

    private static final long serialVersionUID = 1L;

    public DuplicatedKeyInConfigurationException(String message) {
        super(message);
    }

    public static DuplicatedKeyInConfigurationException forMessage(String format, Object... args) {
        return new DuplicatedKeyInConfigurationException(String.format(format, args));
    }

}
