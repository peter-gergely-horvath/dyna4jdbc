package com.github.dyna4jdbc.integrationtests;

import java.io.StringWriter;
import java.sql.*;

/**
 * @author Peter Horvath
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

    }
    
    static String newLineSeparated(String... inputStrings) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String string : inputStrings) {
            stringBuilder.append(string).append(LINE_SEPARATOR);
        }
        return stringBuilder.toString();
    }
}
