package com.github.dyna4jdbc.internal.sqlstate;

public enum SQLState {

    SUCCESSFUL_COMPLETION(SQLStateClass.SUCCESS, "000"),


    CLIENT_ABORT(SQLStateClass.WARNING, "001"),


    SCRIPT_EXECUTION_ERROR(SQLStateClass.DYNAMIC_SQL_ERROR, "001"),
    RESULT_SET_MULTIPLE_EXPECTED_ONE(SQLStateClass.DYNAMIC_SQL_ERROR, "100"),


    ERROR_CONNECTION_UNABLE_TO_ESTABILISH(SQLStateClass.ERROR_CONNECTION, "001"),
    ERROR_CONNECTION_REJECTED(SQLStateClass.ERROR_CONNECTION, "004"),
    ERROR_CONNECTION_FAILURE(SQLStateClass.ERROR_CONNECTION, "006"),


    FEATURE_NOT_SUPPORTED(SQLStateClass.FEATURE_NOT_SUPPORTED, "000"),


    ERROR_DATA_CONVERSION_FAILED(SQLStateClass.ERROR_DATA_EXCEPTION, "018"),
    ERROR_DATA_CONVERSION_NOT_SUPPORTED(SQLStateClass.ERROR_DATA_EXCEPTION, "021"),


    ERROR_USING_STDOUT_FROM_UPDATE(SQLStateClass.EXTERNAL_ROUTINE_INVOCATION_EXCEPTION, "001"),


    SYNTAX_ERROR(SQLStateClass.SYNTAX_OR_ACCESS_RULE_ERROR, "000"),
    INCONSISTENT_HEADER_SPECIFICATION(SQLStateClass.SYNTAX_OR_ACCESS_RULE_ERROR, "001"),
    INVALID_FORMATTING_HEADER(SQLStateClass.SYNTAX_OR_ACCESS_RULE_ERROR, "002"),


    CLIENT_API_CONFIGURATION_ERROR(SQLStateClass.CLIENT_ERROR, "001"),
    CLIENT_API_CALLER_ERROR(SQLStateClass.CLIENT_ERROR, "002"),


    CLOSE_FAILED(SQLStateClass.SYSTEM_ERROR, "001"),
    CANCEL_FAILED(SQLStateClass.SYSTEM_ERROR, "002"),
    LOADING_SCRIPTENGINE_FAILED(SQLStateClass.SYSTEM_ERROR, "100"),
    UNEXPECTED_THROWABLE(SQLStateClass.SYSTEM_ERROR, "998"),
    UNEXPECTED_STATE_REACHED(SQLStateClass.SYSTEM_ERROR, "999");

    //CHECKSTYLE.OFF: VisibilityModifier
    public final String code;

    final Category category;

    final String classCode;
    final String subClassCode;
    //CHECKSTYLE.ON: VisibilityModifier

    SQLState(SQLStateClass sqlStateClass, String subClassCode) {
        this.category = sqlStateClass.category;
        this.classCode = sqlStateClass.classCode;
        this.subClassCode = subClassCode;

        this.code = String.format("%s%s", classCode, subClassCode);
    }
}
