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

public class JRubyScriptEngineTest extends AbstractScriptEngineIntegrationTest {

    protected JRubyScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:jruby");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "puts \"Hello World\" ";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "$msg = \"Hello World\" ";
        String printVariableScript = "puts \"Message::\" \n puts $msg ";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String script = "puts\"A:\tB:\"\n puts \"First A\tFirst B\"\n puts \"Second A\tSecond B\" ";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String script = "puts\"A::\tB::\"\n puts \"First A\tFirst B\"\n puts \"Second A\tSecond B\" ";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("puts \"A:\tB:\"\n")
                .append("puts \"First A\tFirst B\"\n")
                .append("puts \"Second A\tSecond B\"\n")
                .append("puts \"Third A\tThird B\"\n")
                .append("puts \"Fourth A\tFourth B\"\n")
                .append("puts \"Fifth A\tFifth B\"\n")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }
    
    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "puts\"Message::\"\n puts $parameter1";

         assertPreparedStatementQueryReturnsParameter(script);
    }
}
