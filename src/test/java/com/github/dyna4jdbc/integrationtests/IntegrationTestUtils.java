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

                    stringWriter.append(String.format("RESULT SET #%s %n", rsCount));
                    ResultSetMetaData metaData = rs.getMetaData();
                    final int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        int columnDisplaySize = metaData.getColumnDisplaySize(i);
                        String formatStr = "%" + columnDisplaySize + "s | ";
                        stringWriter.append(String.format(formatStr, columnLabel));
                    }
                    stringWriter.append("\n");

                    for (int i = 1; i <= columnCount; i++) {
                        int columnDisplaySize = metaData.getColumnDisplaySize(i);
                        String formatStr = "%" + columnDisplaySize + "s | ";
                        stringWriter.append(String.format(formatStr, "").replace(' ', '-'));
                    }
                    stringWriter.append("\n");


                    while (rs.next()) {

                        for (int i = 1; i <= columnCount; i++) {
                            int columnDisplaySize = metaData.getColumnDisplaySize(i);
                            String formatStr = "%" + columnDisplaySize + "s | ";
                            stringWriter.append(String.format(formatStr, rs.getString(i)));
                        }
                        stringWriter.append("\n");
                    }

                }

                results = statement.getMoreResults();
            }

            return stringWriter.toString();
        }

    }
}
