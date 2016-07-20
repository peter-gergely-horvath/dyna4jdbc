package com.github.dyna4jdbc.internal.sqlstate;

/**
 * @author Peter Horvath
 */
enum SQLStateClass {

    SUCCESS(Category.SUCCESS, "00", "Completed successfully"),

    WARNING(Category.WARNING, "01", "Warning"),

    NOT_FOUND(Category.NO_DATA, "02", "No data"),

    ERROR_CONNECTION(Category.ERROR, "08", "Connection error"),

    ERROR_DATA_EXCEPTION(Category.ERROR, "22", "Data exception");

    //CHECKSTYLE.OFF: VisibilityModifier
    final Category category;

    final String classCode;
    final String classText;
    //CHECKSTYLE.ON: VisibilityModifier

    SQLStateClass(Category category, String classCode, String classText) {
        this.category = category;
        this.classCode = classCode;
        this.classText = classText;
    }
}
