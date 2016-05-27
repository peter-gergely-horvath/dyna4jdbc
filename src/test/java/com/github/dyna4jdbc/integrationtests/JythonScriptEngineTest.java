package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.*;
import static org.testng.Assert.*;


public class JythonScriptEngineTest {

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:jython")) {

            try(Statement statement = connection.createStatement()) {

                statement.executeUpdate("print \"Hello World\" ");
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

        String expectedOutput = newLineSeparated(
                        "RESULT SET #1 ", 
                        "        Message | ",
                        "----------------|-",
                        "    Hello World | ");

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:jython")) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate("msg = \"Hello World\"; ");
            }

            String resultSetString = executeScriptForResultSetString(
                    "print \"Message::\" \nprint msg ", connection);

            assertEquals(resultSetString, expectedOutput);
        }

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                        "              1 |               2 | ", 
                        "----------------|-----------------|-", 
                        "             A: |              B: | ", 
                        "        First A |         First B | ", 
                        "       Second A |        Second B | ");


        String url = "jdbc:dyna4jdbc:scriptengine:jython";

        String script = "print \"A:\tB:\" \nprint \"First A\tFirst B\" \nprint \"Second A\tSecond B\"";

        String resultSetString = executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                "              A |               B | ",
                "----------------|-----------------|-",
                "        First A |         First B | ",
                "       Second A |        Second B | ");


        String url = "jdbc:dyna4jdbc:scriptengine:jython";

        String script = "print \"A::\tB::\" \nprint \"First A\tFirst B\" \nprint \"Second A\tSecond B\"";

        String resultSetString = executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

}
