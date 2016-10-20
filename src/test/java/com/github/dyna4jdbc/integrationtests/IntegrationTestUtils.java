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

 
package com.github.dyna4jdbc.integrationtests;

import java.io.StringWriter;
import java.sql.*;

/**
 * @author Peter G. Horvath
 */
public class IntegrationTestUtils {

    private IntegrationTestUtils() {
        throw new AssertionError("static utility class");
    }
    
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");


    static String executeScriptForResultSetString(String url, String script) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url)) {

            return executeScriptForResultSetString(script, connection);
        }
    }

    static String executeScriptForResultSetString(String script, Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement();) {

            return executeScriptForResultSetString(script, statement);
        }

    }

    static String executeScriptForResultSetString(String script, Statement statement) throws SQLException {

        boolean results = statement.execute(script);
        int rsCount = 0;

        StringWriter stringWriter = new StringWriter();

        while (results) {
            try (ResultSet rs = statement.getResultSet()) {

                rsCount++;

                stringWriter.append(String.format("RESULT SET #%s %s", rsCount, LINE_SEPARATOR));
                ResultSetMetaData metaData = rs.getMetaData();
                final int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    int columnDisplaySize = metaData.getColumnDisplaySize(i);
                    String formatStr = "%" + columnDisplaySize + "s | ";
                    stringWriter.append(String.format(formatStr, columnLabel));
                }
                stringWriter.append(LINE_SEPARATOR);

                for (int i = 1; i <= columnCount; i++) {
                    int columnDisplaySize = metaData.getColumnDisplaySize(i);
                    String formatStr = "%" + columnDisplaySize + "s | ";
                    stringWriter.append(String.format(formatStr, "").replace(' ', '-'));
                }
                stringWriter.append(LINE_SEPARATOR);


                while (rs.next()) {

                    for (int i = 1; i <= columnCount; i++) {
                        int columnDisplaySize = metaData.getColumnDisplaySize(i);
                        String formatStr = "%" + columnDisplaySize + "s | ";
                        stringWriter.append(String.format(formatStr, rs.getString(i)));
                    }
                    stringWriter.append(LINE_SEPARATOR);
                }

            }

            results = statement.getMoreResults();
        }

        return stringWriter.toString();
    }

    static String newLineSeparated(String... inputStrings) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String string : inputStrings) {
            stringBuilder.append(string).append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
