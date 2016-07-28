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
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "print(\"Message::\");\n print(parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }
}
