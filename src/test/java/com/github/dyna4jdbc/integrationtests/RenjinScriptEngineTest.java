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

public class RenjinScriptEngineTest extends IntegrationTestBase {

    protected RenjinScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:Renjin");
    }

    @Test
    @Override
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "cat('Hello World') ";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    @Override
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "msg <- \"Hello World\" ";
        String printVariableScript = "cat(\"Message::\n\") \n cat(msg) ";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    @Override
    public void testHeadersNotSpecified() throws Exception {

        String script = "cat(\"A:\tB:\n\")\n cat(\"First A\tFirst B\n\")\n cat(\"Second A\tSecond B\")";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    @Override
    public void testHeadersSpecified() throws Exception {

        String script = "cat(\"A::\tB::\n\")\n cat(\"First A\tFirst B\n\")\n cat(\"Second A\tSecond B\")";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingWithHeaders() throws Exception {

        String script = new StringBuilder()
                .append("cat (\"A::\tB::\n\")\n")
                .append("cat (\"First A\tFirst B\n\")\n")
                .append("cat (\"Second A\tSecond B\n\")\n")
                .append("cat (\"Third A\tThird B\n\")\n")
                .append("cat (\"Fourth A\tFourth B\n\")\n")
                .append("cat (\"Fifth A\tFifth B\n\")\n")
                .toString();

        assertYieldsFirstTwoRowsOnlyWithHeaders(script);
    }
    
    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("cat (\"A:\tB:\n\")\n")
                .append("cat (\"First A\tFirst B\n\")\n")
                .append("cat (\"Second A\tSecond B\n\")\n")
                .append("cat (\"Third A\tThird B\n\")\n")
                .append("cat (\"Fourth A\tFourth B\n\")\n")
                .append("cat (\"Fifth A\tFifth B\n\")\n")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }
    
    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "cat(\"Message::\n\") \n cat(parameter1) ";
        
        assertPreparedStatementQueryReturnsParameter(script);
        
        assertPreparedStatementQueryReturnsParameter(script);
    }
}
