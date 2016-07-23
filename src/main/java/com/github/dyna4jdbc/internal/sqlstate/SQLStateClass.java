package com.github.dyna4jdbc.internal.sqlstate;

/**
 * @author Peter Horvath
 */
public enum SQLStateClass {

    SUCCESS(Category.SUCCESS, "00", "Completed successfully"),
    WARNING(Category.WARNING, "01", "Warning"),
    NOT_FOUND(Category.NO_DATA, "02", "No data"),
    DYNAMIC_SQL_ERROR(Category.ERROR, "07", "Dynamic SQL error"),
    ERROR_CONNECTION(Category.ERROR, "08", "Connection error"),
    FEATURE_NOT_SUPPORTED(Category.ERROR, "0A", "Feature not supported"),
    ERROR_DATA_EXCEPTION(Category.ERROR, "22", "Data exception"),
    EXTERNAL_ROUTINE_INVOCATION_EXCEPTION(Category.ERROR, "39", "External routine invocation exception"),
    SYNTAX_OR_ACCESS_RULE_ERROR(Category.ERROR, "42", "Syntax error or access rule violation"),
    CLIENT_ERROR(Category.ERROR, "56", "Client error"),
    SYSTEM_ERROR(Category.ERROR, "57", "System error");


    //CHECKSTYLE.OFF: VisibilityModifier
    public final Category category;

    public final String classCode;
    final String classText;
    //CHECKSTYLE.ON: VisibilityModifier

    SQLStateClass(Category category, String classCode, String classText) {
        this.category = category;
        this.classCode = classCode;
        this.classText = classText;
    }
}
