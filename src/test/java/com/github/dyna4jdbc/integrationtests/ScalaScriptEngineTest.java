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

import org.testng.annotations.Test;

import java.sql.SQLException;

public class ScalaScriptEngineTest extends IntegrationTestBase {

    protected ScalaScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:scala");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "println(\"Hello World\")";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = " var msg : String = \"Hello World\" ";
        String printVariableScript = "println(\"Message::\");\n println(msg) ";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    @Override
    public void testHeadersNotSpecified() throws Exception {

        String script = "println(\"A:\tB:\") \n println(\"First A\tFirst B\") \n println(\"Second A\tSecond B\");";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    @Override
    public void testHeadersSpecified() throws Exception {

        String script = "println(\"A::\tB::\") \n println(\"First A\tFirst B\") \n print(\"Second A\tSecond B\")";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }
    
    @Test
    @Override
    public void testStatementMaxRowsHandlingWithHeaders() throws Exception {

        String script = new StringBuilder()
                .append("println (\"A::\tB::\")\n")
                .append("println (\"First A\tFirst B\")\n")
                .append("println (\"Second A\tSecond B\")\n")
                .append("println (\"Third A\tThird B\")\n")
                .append("println (\"Fourth A\tFourth B\")\n")
                .append("println (\"Fifth A\tFifth B\")\n")
                .toString();

        assertYieldsFirstTwoRowsOnlyWithHeaders(script);
    }
    
    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("println (\"A:\tB:\")\n")
                .append("println (\"First A\tFirst B\")\n")
                .append("println (\"Second A\tSecond B\")\n")
                .append("println (\"Third A\tThird B\")\n")
                .append("println (\"Fourth A\tFourth B\")\n")
                .append("println (\"Fifth A\tFifth B\")\n")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }

    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "println(\"Message::\") \n println(parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }
}
