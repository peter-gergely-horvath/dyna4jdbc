package com.github.dyna4jdbc.integrationtests;

import org.testng.annotations.Test;

import java.sql.SQLException;


public class JythonScriptEngineTest extends AbstractScriptEngineIntegrationTest {

    protected JythonScriptEngineTest() {
        super("jdbc:dyna4jdbc:scriptengine:jython");
    }

    @Test
    public void testWritingFromUpdateThrowsSQLException() {

        String script = "print \"Hello World\" ";

        assertWritingFromUpdateThrowsSQLException(script);
    }

    @Test
    public void testVariableDeclaredInStatementVisibleFromAnotherStatement() throws SQLException {

        String variableDeclarationScript = "msg = \"Hello World\"; ";
        String printVariableScript = "print \"Message::\" \nprint msg ";

        assertVariableDeclaredInStatementVisibleFromAnotherStatement(variableDeclarationScript, printVariableScript);

    }

    @Test
    public void testHeadersNotSpecified() throws Exception {

        String script = "print \"A:\tB:\" \nprint \"First A\tFirst B\" \nprint \"Second A\tSecond B\"";

        assertHeadersNotSpecifiedCausesNumbersToBeUsed(script);
    }

    @Test
    public void testHeadersSpecified() throws Exception {

        String script = "print \"A::\tB::\" \nprint \"First A\tFirst B\" \nprint \"Second A\tSecond B\"";

        assertIfHeadersAreSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    @Override
    public void testPreparedStatementBindsVariable() throws Exception {

        String script = "print \"Message::\" \nprint parameter1";

        assertPreparedStatementQueryReturnsParameter(script);
    }
}
