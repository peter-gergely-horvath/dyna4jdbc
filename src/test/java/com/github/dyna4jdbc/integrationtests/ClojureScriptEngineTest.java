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

        assertIfHeadersSpecifiedThenHeadersAreUsed(script);
    }

}
