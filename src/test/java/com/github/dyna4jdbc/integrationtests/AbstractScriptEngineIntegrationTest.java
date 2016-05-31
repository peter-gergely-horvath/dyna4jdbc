package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.executeScriptForResultSetString;
import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.newLineSeparated;
import static org.testng.Assert.*;

/**
 * @author Peter Horvath
 */
public class AbstractScriptEngineIntegrationTest {

    private final String jdbcUrl;

    protected AbstractScriptEngineIntegrationTest(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    protected void assertWritingFromUpdateThrowsSQLException(String script) {
        try(Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try(Statement statement = connection.createStatement()) {

                statement.executeUpdate(script);
            }

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.USING_STDOUT_FROM_UPDATE.name().toString()), message);
        }
    }

    protected void assertVariableDeclaredInStatementVisibleFromAnotherStatement(
            String variableDeclarationScript, String printVariableScript) throws SQLException {
        String expectedOutput = newLineSeparated(
                        "RESULT SET #1 ",
                        "    Message | ",
                        "------------|-",
                        "Hello World | ");

        try(Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (Statement statement = connection.createStatement()) {


                statement.executeUpdate(variableDeclarationScript);
            }

            String resultSetString = executeScriptForResultSetString(printVariableScript, connection);

            assertEquals(resultSetString, expectedOutput);
        }
    }

    protected void assertHeadersNotSpecifiedCausesNumbersToBeUsed(String script) throws SQLException {
        String expectedOutput = newLineSeparated(
                        "RESULT SET #1 ",
                        "       1 |        2 | ",
                        "---------|----------|-",
                        "      A: |       B: | ",
                        " First A |  First B | ",
                        "Second A | Second B | ");

        String resultSetString = executeScriptForResultSetString(jdbcUrl, script);

        assertEquals(resultSetString, expectedOutput);
    }

    protected void assertIfHeadersSpecifiedThenHeadersAreUsed(String script) throws SQLException {
        String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                "       A |        B | ",
                "---------|----------|-",
                " First A |  First B | ",
                "Second A | Second B | ");

        String resultSetString = executeScriptForResultSetString(jdbcUrl, script);

        assertEquals(resultSetString, expectedOutput);
    }
}
