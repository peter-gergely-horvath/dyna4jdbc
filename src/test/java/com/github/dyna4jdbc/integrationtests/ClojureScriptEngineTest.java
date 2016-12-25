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

public class ClojureScriptEngineTest extends AbstractScriptEngineIntegrationTest {

    protected ClojureScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:Clojure");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "(println \"Hello world.\")";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "(def msg \"Hello World\")";
        String printVariableScript = "(println \"Message::\") (println msg)";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String script = "(println \"A:\tB:\")\n (println \"First A\tFirst B\")\n (println \"Second A\tSecond B\")\n";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String script = "(println \"A::\tB::\")\n (println \"First A\tFirst B\")\n (println \"Second A\tSecond B\")\n";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }
    
    @Test
    @Override
    public void testStatementMaxRowsHandlingNoHeaders() throws Exception {

        String script = new StringBuilder()
                .append("(println \"A:\tB:\")\n")
                .append("(println \"First A\tFirst B\")\n")
                .append("(println \"Second A\tSecond B\")\n")
                .append("(println \"Third A\tThird B\")\n")
                .append("(println \"Fourth A\tFourth B\")\n")
                .append("(println \"Fifth A\tFifth B\")\n")
                .toString();

        assertYieldsFirstTwoRowsOnlyNoHeaders(script);
    }

    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "(println \"Message::\")\n (print parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }
}
