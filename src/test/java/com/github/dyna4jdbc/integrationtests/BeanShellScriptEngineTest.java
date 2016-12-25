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

public class BeanShellScriptEngineTest extends AbstractScriptEngineIntegrationTest {

    protected BeanShellScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:beanshell");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "print(\"Hello World\")";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = " msg = \"Hello World\"; ";
        String printVariableScript = "print(\"Message::\");\n print(msg) ";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String script = "print(\"A:\tB:\");\n print(\"First A\tFirst B\");\n print(\"Second A\tSecond B\");\n";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String script = "print(\"A::\tB::\");\n print(\"First A\tFirst B\");\n print(\"Second A\tSecond B\");";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    
    @Test
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("print(\"A:\tB:\");\n ")
                .append("print(\"First A\tFirst B\");\n ")
                .append("print(\"Second A\tSecond B\");")
                .append("print(\"Third A\tThird B\");")
                .append("print(\"Fourth A\tFourth B\");")
                .append("print(\"Fifth A\tFifth B\");")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }
    
    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "print(\"Message::\");\n print(parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }
}
