package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import java.sql.*;

import static org.testng.Assert.*;

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

        String expectedOutput =
                        "RESULT SET #1 \n" +
                        "        Message | \n" +
                        "----------------|-\n" +
                        "    Hello World | \n";

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:scala")) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(" var msg : String = \"Foo\" ");
            }

            String resultSetString = IntegrationTestUtils.executeScriptForResultSetString(
                    "println(\"Message::\"); println(msg) ", connection);

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


        String url = "jdbc:dyna4jdbc:scriptengine:scala";

        String script = "println(\"A:\tB:\") \n println(\"First A\tFirst B\") \n println(\"Second A\tSecond B\");";

        String resultSetString = IntegrationTestUtils.executeScriptForResultSetString(url, script);

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


        String url = "jdbc:dyna4jdbc:scriptengine:scala";

        String script = "println(\"A::\tB::\") \n println(\"First A\tFirst B\") \n print(\"Second A\tSecond B\")";

        String resultSetString = IntegrationTestUtils.executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

}
