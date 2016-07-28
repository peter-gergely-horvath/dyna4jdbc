package com.github.dyna4jdbc.integrationtests;

import java.sql.SQLException;

import org.testng.annotations.Test;

public class JavaScriptScriptEngineTest extends AbstractScriptEngineIntegrationTest {

    protected JavaScriptScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:JavaScript");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "print(\"Hello World\")";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "var msg = \"Hello World\"; ";
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
    public void testStatementMaxRowsHandlingWithHeaders() throws Exception {

        String script = new StringBuilder()
                .append("print(\"A::\tB::\");\n ")
                .append("print(\"First A\tFirst B\");\n ")
                .append("print(\"Second A\tSecond B\");")
                .append("print(\"Third A\tThird B\");")
                .append("print(\"Fourth A\tFourth B\");")
                .append("print(\"Fifth A\tFifth B\");")
                .toString();

        assertYieldsFirstTwoRowsOnlyWithHeaders(script);
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
