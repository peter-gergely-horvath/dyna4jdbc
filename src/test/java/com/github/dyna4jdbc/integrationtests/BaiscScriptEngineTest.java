package com.github.dyna4jdbc.integrationtests;

import java.io.*;
import java.sql.*;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BaiscScriptEngineTest {

    @Test
    public void testUnspecifiedScriptEngineThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.CONNECT_FAILED_EXCEPTION.toString()), message);
            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.toString()), message);
        }
    }

    @Test
    public void testInvalidScriptEngineThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:foobar");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.CONNECT_FAILED_EXCEPTION.toString()), message);
            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.toString()), message);
        }
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript")) {

            try(Statement statement = connection.createStatement()) {

                statement.executeUpdate("print('Hello World')");
            }

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.USING_STDOUT_FROM_UPDATE.toString()), message);
        }
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String expectedOutput =
                        "RESULT SET #1 \n" +
                        "        Message | \n" +
                        "----------------|-\n" +
                        "    Hello World | \n";

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript")) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(" var msg = 'Hello World'; ");
            }

            String resultSetString = executeScriptForResultSetString("print('Message::'); print(msg) ", connection);

            assertEquals(resultSetString, expectedOutput);
        }

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String expectedOutput =
                "RESULT SET #1 \n" +
                        "              1 |               2 | \n" +
                        "----------------|-----------------|-\n" +
                        "             A: |              B: | \n" +
                        "        First A |         First B | \n" +
                        "       Second A |        Second B | \n";


        String url = "jdbc:dyna4jdbc:scriptengine:JavaScript";

        String script = "print('A:\tB:') ; print('First A\tFirst B'); print('Second A\tSecond B')";

        String resultSetString = executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String expectedOutput =
                "RESULT SET #1 \n" +
                "              A |               B | \n" +
                "----------------|-----------------|-\n" +
                "        First A |         First B | \n" +
                "       Second A |        Second B | \n";


        String url = "jdbc:dyna4jdbc:scriptengine:JavaScript";

        String script = "print('A::\tB::') ; print('First A\tFirst B'); print('Second A\tSecond B')";

        String resultSetString = executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

    private static String executeScriptForResultSetString(String url, String script) throws SQLException {
        try(Connection connection = DriverManager.getConnection(url)) {

            return executeScriptForResultSetString(script, connection);
        }
    }

    private static String executeScriptForResultSetString(String script, Connection connection) throws SQLException {
        String result;Statement statement = connection.createStatement();

        boolean results = statement.execute(script);
        int rsCount = 0;

        StringWriter stringWriter = new StringWriter();

        while (results) {
            try (ResultSet rs = statement.getResultSet()) {

                rsCount++;

                stringWriter.append(String.format("RESULT SET #%s %n", rsCount));
                ResultSetMetaData metaData = rs.getMetaData();
                final int columnCount = metaData.getColumnCount();

                for(int i=1; i<=columnCount; i++ ) {
                    String columnLabel = metaData.getColumnLabel(i);
                    int columnDisplaySize = metaData.getColumnDisplaySize(i);
                    String formatStr = "%" + columnDisplaySize + "s | ";
                    stringWriter.append(String.format(formatStr, columnLabel));
                }
                stringWriter.append("\n");

                for(int i=1; i<=columnCount; i++ ) {
                    int columnDisplaySize = metaData.getColumnDisplaySize(i);
                    String formatStr = "%" + columnDisplaySize + "s | ";
                    stringWriter.append(String.format(formatStr, "").replace(' ', '-'));
                }
                stringWriter.append("\n");


                while (rs.next()) {

                    for(int i=1; i<=columnCount; i++ ) {
                        int columnDisplaySize = metaData.getColumnDisplaySize(i);
                        String formatStr = "%" + columnDisplaySize + "s | ";
                        stringWriter.append(String.format(formatStr, rs.getString(i)));
                    }
                    stringWriter.append("\n");
                }

            }

            results = statement.getMoreResults();
        }

        result = stringWriter.toString();
        return result;
    }
}
