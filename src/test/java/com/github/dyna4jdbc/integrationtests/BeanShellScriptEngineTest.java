package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtil;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.testng.Assert.*;

public class BeanShellScriptEngineTest {

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:beanshell")) {

            try(Statement statement = connection.createStatement()) {

                statement.executeUpdate("print(\"Hello World\")");
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

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:beanshell")) {

            try (Statement statement = connection.createStatement()) {

                statement.executeUpdate(" msg = \"Hello World\"; ");
            }

            String resultSetString = IntegrationTestUtils.executeScriptForResultSetString(
                    "print(\"Message::\");\n print(msg) ", connection);

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


        String url = "jdbc:dyna4jdbc:scriptengine:beanshell";

        String script = "print(\"A:\tB:\");\n print(\"First A\tFirst B\");\n print(\"Second A\tSecond B\");\n";

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


        String url = "jdbc:dyna4jdbc:scriptengine:beanshell";

        String script = "print(\"A::\tB::\");\n print(\"First A\tFirst B\");\n print(\"Second A\tSecond B\");";

        String resultSetString = IntegrationTestUtils.executeScriptForResultSetString(url, script);

        assertEquals(resultSetString, expectedOutput);
    }

}
