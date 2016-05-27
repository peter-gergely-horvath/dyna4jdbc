package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import java.sql.*;

import static org.testng.Assert.*;
import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.*;

public class ScalaScriptEngineTest {

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:scala")) {

            try(Statement statement = connection.createStatement()) {

                statement.executeUpdate("println(\"Hello World\")");
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
                        "            Foo | ");

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:scala")) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(" var msg : String = \"Foo\" ");
            }

            String resultSetString = executeScriptForResultSetString(
                    "println(\"Message::\"); println(msg) ", connection);

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


        String url = "jdbc:dyna4jdbc:scriptengine:scala";

        String script = "println(\"A:\tB:\") \n println(\"First A\tFirst B\") \n println(\"Second A\tSecond B\");";

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


        String url = "jdbc:dyna4jdbc:scriptengine:scala";

        String script = "println(\"A::\tB::\") \n println(\"First A\tFirst B\") \n print(\"Second A\tSecond B\")";

        String resultSetString = executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

}
