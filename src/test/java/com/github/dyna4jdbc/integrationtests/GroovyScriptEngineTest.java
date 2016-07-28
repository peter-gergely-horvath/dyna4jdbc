package com.github.dyna4jdbc.integrationtests;

import org.testng.annotations.Test;

import java.sql.SQLException;

public class GroovyScriptEngineTest extends AbstractScriptEngineIntegrationTest {

    protected GroovyScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:Groovy");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "println('Hello World') ";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "msg = 'Hello World' ";
        String printVariableScript = "println ('Message::');\n println(msg)";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String script = "println('A:\tB:'); println('First A\tFirst B'); println('Second A\tSecond B');";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String script = "println('A::\tB::'); println('First A\tFirst B'); println('Second A\tSecond B');";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "println('Message::'); println(parameter1)";

        assertPreparedStatementQueryReturnsParameter(script);
    }
}
