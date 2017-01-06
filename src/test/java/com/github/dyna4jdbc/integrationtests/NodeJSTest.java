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

import java.sql.SQLException;

public class NodeJSTest extends IntegrationTestBase {

    protected NodeJSTest() {
        super("jdbc:dyna4jdbc:nodejs");
    }

    @Test
    @Override
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "console.log(\"Hello World\")";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    @Override
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "msg = \"Hello World\"; ";

        String printVariableScript = new StringBuilder()
                .append("console.log(\"Message::\");\n ")
                .append("console.log(msg); \n")
                .toString();

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    @Override
    public void testHeadersNotSpecified() throws Exception {

        String script = "console.log(\"A:\tB:\");\n console.log(\"First A\tFirst B\");\n console.log(\"Second A\tSecond B\");\n";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    @Override
    public void testHeadersSpecified() throws Exception {

        String script = "console.log(\"A::\tB::\");\n console.log(\"First A\tFirst B\");\n console.log(\"Second A\tSecond B\");";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingWithHeaders() throws Exception {

        String script = new StringBuilder()
                .append("console.log(\"A::\tB::\");\n ")
                .append("console.log(\"First A\tFirst B\");\n ")
                .append("console.log(\"Second A\tSecond B\");")
                .append("console.log(\"Third A\tThird B\");")
                .append("console.log(\"Fourth A\tFourth B\");")
                .append("console.log(\"Fifth A\tFifth B\");")
                .toString();

        assertYieldsFirstTwoRowsOnlyWithHeaders(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("console.log(\"A:\tB:\");\n ")
                .append("console.log(\"First A\tFirst B\");\n ")
                .append("console.log(\"Second A\tSecond B\");")
                .append("console.log(\"Third A\tThird B\");")
                .append("console.log(\"Fourth A\tFourth B\");")
                .append("console.log(\"Fifth A\tFifth B\");")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }



    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "console.log(\"Message::\");\n console.log(parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }

}
