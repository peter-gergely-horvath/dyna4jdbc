package com.github.dyna4jdbc.internal.config;

public class InvalidConfigurationValueException extends MisconfigurationException {

    private static final long serialVersionUID = 1L;

    public InvalidConfigurationValueException(String message) {
        super(message);
    }

    public static InvalidConfigurationValueException forMessage(String format, Object... args) {
        return new InvalidConfigurationValueException(String.format(format, args));
    }

}
