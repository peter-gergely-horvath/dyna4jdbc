package com.github.dyna4jdbc.internal.sqlstate;

public enum SQLState {

    SUCCESSFUL_COMPLETION(
            SQLStateClass.SUCCESS, "000", "Completed successfully"),

    CLIENT_ABORT(
            SQLStateClass.WARNING, "001", "Client abort"),
    
    DYNAMIC_SQL_ERROR(
            Category.ERROR, "07", "Dynamic SQL error"),

    ERROR_CONNECTION_UNABLE_TO_ESTABILISH(
            SQLStateClass.ERROR_CONNECTION, "001", "Unable to establish connection"),
    ERROR_CONNECTION_REJECTED(
            SQLStateClass.ERROR_CONNECTION, "004", "Connection rejected"),
    ERROR_CONNECTION_FAILURE(
            SQLStateClass.ERROR_CONNECTION, "006", "Connection failure"),

    FEATURE_NOT_SUPPORTED(
            Category.ERROR, "0A", "Feature not supported"),

    ERROR_DATA_CONVERSION_FAILED(
            SQLStateClass.ERROR_DATA_EXCEPTION, "018", "The conversion failed"),
    ERROR_DATA_CONVERSION_NOT_SUPPORTED(
            SQLStateClass.ERROR_DATA_EXCEPTION, "021", "The conversion is not supported"),

    EXTERNAL_ROUTINE_INVOCATION_EXCEPTION(
            Category.ERROR, "39", "External routine invocation exception"),

    SYNTAX_OR_ACCESS_RULE_ERROR(
            Category.ERROR, "42", "Syntax error or access rule violation"),


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

    // TODO: this should be removed: each SQLState should belong to a SQLStateClass
    SQLState(Category category, String classCode, String classText) {
        this.category = category;
        this.classCode = classCode;
        this.classText = classText;
        this.subClassCode = EMPTY_SUBCLASS_CODE;
        this.subClassText = null;

        this.code = String.format("%s%s", classCode, subClassCode);
    }

    SQLState(SQLStateClass sqlStateClass, String subClassCode, String subClassText) {
        this.category = sqlStateClass.category;
        this.classCode = sqlStateClass.classCode;
        this.classText = sqlStateClass.classText;
        this.subClassCode = subClassCode;
        this.subClassText = subClassText;

        this.code = String.format("%s%s", classCode, subClassCode);
    }
}
