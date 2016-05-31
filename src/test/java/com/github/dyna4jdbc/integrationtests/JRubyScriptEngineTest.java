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

        assertIfHeadersSpecifiedThenHeadersAreUsed(script);
    }

}
