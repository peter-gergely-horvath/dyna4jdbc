/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.executeScriptForResultSetString;
import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.newLineSeparated;
import static org.testng.Assert.assertEquals;

public class InterpreterScriptEngineTest extends IntegrationTestBase {

    protected InterpreterScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:JavaScript");
    }

    @Test
    @Override
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "dyna4jdbc:set ScriptEngine Groovy \n"
                + "println \"Hello World\" ";   // NOTE: this is Groovy syntax, it would not work with JavaScript

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    @Override
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "var msg = \"Hello World\"; dyna4jdbc:set ScriptEngine Groovy";

        // NOTE: this is Groovy syntax, it would not work with JavaScript
        String printVariableScript = "println \"Message::\";\n println msg ";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    @Override
    public void testHeadersNotSpecified() throws Exception {

        String script = "dyna4jdbc:set ScriptEngine Groovy\n" +
                // NOTE: this is Groovy syntax, it would not work with JavaScript
                "println \"A:\tB:\";\n println \"First A\tFirst B\";\n println \"Second A\tSecond B\";\n";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    @Override
    public void testHeadersSpecified() throws Exception {

        String script = "dyna4jdbc:set ScriptEngine Groovy \n" +
                // NOTE: this is Groovy syntax, it would not work with JavaScript
                "println \"A::\tB::\";\n println \"First A\tFirst B\";\n println \"Second A\tSecond B\";";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    public void testStatementMaxRowsHandlingWithHeaders() throws Exception {

        String script = new StringBuilder()
                .append("print(\"A::\tB::\");\n ")
                .append("print(\"First A\tFirst B\");\n ")
                .append("print(\"Second A\tSecond B\");")
                .append("dyna4jdbc:set ScriptEngine Groovy\n")
                .append("println \"Third A\tThird B\";")
                .append("println \"Fourth A\tFourth B\";")
                .append("println \"Fifth A\tFifth B\";")
                .toString();

        assertYieldsFirstTwoRowsOnlyWithHeaders(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("print(\"A:\tB:\");\n ")
                .append("dyna4jdbc:set ScriptEngine Groovy\n")
                .append("println \"First A\tFirst B\" ;\n ")
                .append("println \"Second A\tSecond B\";")
                .append("println \"Third A\tThird B\";")
                .append("println \"Fourth A\tFourth B\";")
                .append("println \"Fifth A\tFifth B\";")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }



    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "print(\"Message::\");\n " +
                "dyna4jdbc:set ScriptEngine Groovy\n" +
                "println parameter1";

        assertPreparedStatementQueryReturnsParameter(script);
    }

    @Test
    public void testVariableDeclaredIsCarriedOverToMultipleLanguages() throws Exception {

        try(Connection connection = DriverManager.getConnection(jdbcUrl)) {

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("var msg = \"Hello World\"");
            }

            assertInterpreterCommandAndScriptYields("clojure", connection,
                    "(println \"Message::\") (println msg)");


            assertInterpreterCommandAndScriptYields("Groovy", connection,
                    "println \"Message::\";\n println msg ");

            assertInterpreterCommandAndScriptYields("scala", connection,
                    "println(\"Message::\");\n println(msg)");

            assertInterpreterCommandAndScriptYields("python", connection,
                    "print \"Message::\" \nprint msg ");

            assertInterpreterCommandAndScriptYields("jruby", connection,
                    "puts \"Message::\" \n puts $msg ");
        }

    }

    private void assertInterpreterCommandAndScriptYields(
            String scriptEngineName, Connection connection, String printScript) throws SQLException {

        final String expectedOutput = newLineSeparated(
                "RESULT SET #1 ",
                "    Message | ",
                "------------|-",
                "Hello World | ");

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("dyna4jdbc:set ScriptEngine " + scriptEngineName);
        }

        String resultSetString = executeScriptForResultSetString(printScript, connection);
        assertEquals(resultSetString, expectedOutput);
    }


}
