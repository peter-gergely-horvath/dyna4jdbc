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

import com.github.dyna4jdbc.internal.JDBCError;

import java.sql.*;

import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.executeScriptForResultSetString;
import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.newLineSeparated;
import static org.testng.Assert.*;

/**
 * @author Peter G. Horvath, Balazs Toeroek
 */
public abstract class AbstractScriptEngineIntegrationTest {

    protected static final String PREPARED_STATEMENT_PARAMETER = "Hello World";
    protected final String jdbcUrl;

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

    protected void assertIfHeadersAreSpecifiedThenHeadersAreUsed(String script) throws SQLException {
        String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                "       A |        B | ",
                "---------|----------|-",
                " First A |  First B | ",
                "Second A | Second B | ");

        String resultSetString = executeScriptForResultSetString(jdbcUrl, script);

        assertEquals(resultSetString, expectedOutput);
    }

    protected void assertYieldsFirstTwoRowsOnlyWithHeaders(String script) throws SQLException {
        String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                "       A |        B | ",
                "---------|----------|-",
                " First A |  First B | ",
                "Second A | Second B | ");


        String resultSetString;
        try(Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (Statement statement = connection.createStatement();) {

                statement.setMaxRows(2);

                resultSetString = executeScriptForResultSetString(script, statement);
            }
        }

        assertEquals(resultSetString, expectedOutput);
    }

    protected void assertYieldsFirstTwoRowsOnlyNoHeaders(String script) throws SQLException {
        String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                "       1 |        2 | ",
                "---------|----------|-",
                "      A: |       B: | ",
                " First A |  First B | ");


        String resultSetString;
        try(Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (Statement statement = connection.createStatement();) {

                statement.setMaxRows(2);

                resultSetString = executeScriptForResultSetString(script, statement);
            }
        }

        assertEquals(resultSetString, expectedOutput);
    }

    protected void assertPreparedStatementQueryReturnsParameter(String script) throws SQLException {

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setString(1, PREPARED_STATEMENT_PARAMETER);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String result = resultSet.getString("Message");
                    assertEquals(result, PREPARED_STATEMENT_PARAMETER);
                }
            }
        }
    }

    public abstract void testPreparedStatementBindsVariable() throws Exception;
}
