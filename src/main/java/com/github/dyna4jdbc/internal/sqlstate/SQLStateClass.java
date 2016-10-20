/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.sqlstate;

/**
 * @author Peter G. Horvath
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
