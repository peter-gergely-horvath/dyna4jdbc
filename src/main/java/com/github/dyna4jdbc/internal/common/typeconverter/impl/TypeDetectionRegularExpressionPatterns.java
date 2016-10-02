package com.github.dyna4jdbc.internal.common.typeconverter.impl;

import java.util.regex.Pattern;

final class TypeDetectionRegularExpressionPatterns {
    
    private TypeDetectionRegularExpressionPatterns() {
        throw new AssertionError(TypeDetectionRegularExpressionPatterns.class + " is a static constants class!");
    }
    
    static final Pattern ONE_BIT = Pattern.compile("^([01])$");
    
    static final Pattern NUMBER_INTEGER = Pattern.compile("^[+-]?(\\d+)$");
    static final Pattern NUMBER_DECIMAL = Pattern.compile("^([+-])?(\\d+)(?:\\.(?<precision>\\d+))?$");
    
    static final Pattern TIMESTAMP = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}:\\d{2}(?:\\.\\d{3})?");

}
