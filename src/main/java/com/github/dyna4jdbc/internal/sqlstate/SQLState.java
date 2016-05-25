package com.github.dyna4jdbc.internal.sqlstate;

public enum SQLState {

    SUCCESSFUL_COMPLETION(Category.SUCCESS, "00", "Completed successfully"),

    WARNING_CLASS(Category.WARNING, "01", "Warning"),

    NOT_FOUND_CLASS(Category.NO_DATA, "02", "No data"),

    DYNAMIC_SQL_ERROR(Category.ERROR, "07", "Dynamic SQL error"),

    ERROR_CONNECTION_CLASS(Category.ERROR, "08", "Connection error"),
    ERROR_CONNECTION_UNABLE_TO_ESTABILISH(SQLState.ERROR_CONNECTION_CLASS, "001", "Unable to establish connection"),
    ERROR_CONNECTION_REJECTED(SQLState.ERROR_CONNECTION_CLASS, "004", "Connection rejected"),
    ERROR_CONNECTION_FAILURE(SQLState.ERROR_CONNECTION_CLASS, "006", "Connection failure"),

    FEATURE_NOT_SUPPORTED(Category.ERROR, "0A", "Feature not supported"),

    ERROR_DATA_EXCEPTION_CLASS(Category.ERROR, "22", "Data exception"),
    ERROR_DATA_CONVERSION_FAILED(ERROR_DATA_EXCEPTION_CLASS, "018", "The conversion is not supported"),
    ERROR_DATA_CONVERSION_NOT_SUPPORTED(ERROR_DATA_EXCEPTION_CLASS, "021", "The conversion is not supported"),

    EXTERNAL_ROUTINE_INVOCATION_EXCEPTION(Category.ERROR, "39", "External routine invocation exception"),

    SYNTAX_OR_ACCESS_RULE_ERROR(Category.ERROR, "42", "Syntax error or access rule violation"),


    // --- NON-STANDARD states ---
    // TODO: investigate SQLSTATE codes further to see if there is any better match for these
    CLIENT_ERROR(Category.ERROR, "56", "Client error"),
    SYSTEM_ERROR(Category.ERROR, "57", "System error");

    //CHECKSTYLE.OFF: VisibilityModifier
    public final String code;

    final Category category;

    final String classCode;
    final String classText;
    final String subClassCode;
    final String subClassText;
    //CHECKSTYLE.ON: VisibilityModifier

    private static final String EMPTY_SUBCLASS_CODE = "000";

    SQLState(Category category, String classCode, String classText) {
        this.category = category;
        this.classCode = classCode;
        this.classText = classText;
        this.subClassCode = EMPTY_SUBCLASS_CODE;
        this.subClassText = null;

        this.code = String.format("%s%s", classCode, subClassCode);
    }

    SQLState(SQLState sqlStateClass, String subClassCode, String subClassText) {
        this.category = sqlStateClass.category;
        this.classCode = sqlStateClass.classCode;
        this.classText = sqlStateClass.classText;
        this.subClassCode = subClassCode;
        this.subClassText = subClassText;

        this.code = String.format("%s%s", classCode, subClassCode);
    }
}
