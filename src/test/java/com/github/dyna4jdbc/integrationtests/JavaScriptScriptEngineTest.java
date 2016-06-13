package com.github.dyna4jdbc.integrationtests;

import java.sql.*;

import org.testng.annotations.Test;

import static com.github.dyna4jdbc.integrationtests.IntegrationTestUtils.executeScriptForResultSetString;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

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

        assertIfHeadersSpecifiedThenHeadersAreUsed(script);
    }

    @Test
    public void testPreparedStatementBindsVariable() throws Exception {

        // variable parameter1 will come from the ScriptContext
        String script = "print(\"Message::\");\n print(parameter1) ";

        // TODO: extract this to AbstractScriptEngineIntegrationTest, make it generic
        // and implement for all script types like other test methods
        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript")) {

            try (PreparedStatement statement = connection.prepareStatement(script)) {
                // value will be bound to the name "parameter1"
                // see OutputHandlingPreparedStatement.setParameter()
                statement.setString(1, "Hello World");

                ResultSet resultSet = statement.executeQuery();

                // TODO: should test the exact value, like other methods do
                assertNotNull(resultSet);

            }
        }

    }

}
