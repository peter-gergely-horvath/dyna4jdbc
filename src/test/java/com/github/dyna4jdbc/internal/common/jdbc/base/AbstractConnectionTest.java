package com.github.dyna4jdbc.internal.common.jdbc.base;

import static com.github.dyna4jdbc.CommonTestUtils.assertThrowsSQLExceptionWithFunctionNotSupportedMessage;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

public class AbstractConnectionTest {

    private AbstractConnection abstractConnection;

    @BeforeMethod
    public void beforeMethod() throws SQLException, MisconfigurationException {

        // we ScriptEngineConnectionwe to test AbstractConnection
        this.abstractConnection = new ScriptEngineConnection("JavaScript", new Properties());
    }

    @Test
    public void testCreateStatement() throws SQLException {

        try (Statement statement = abstractConnection.createStatement()) {
            assertNotNull(statement);
        }
    }

    @Test
    public void testPrepareCall() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.prepareCall("foobar"));
    }

    @Test
    public void testNativeSQL() throws SQLException {

        assertNull(abstractConnection.nativeSQL(null));

        String input = "foobar";

        String nativeSQL = abstractConnection.nativeSQL(input);

        assertEquals(nativeSQL, input);
    }

    @Test
    public void testSetAutoCommit() throws SQLException {

        abstractConnection.setAutoCommit(true);

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.setAutoCommit(false));
    }

    @Test
    public void testGetAutoCommit() throws SQLException {

        assertTrue(abstractConnection.getAutoCommit());
    }

    @Test
    public void testCommit() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.commit());
    }

    @Test
    public void testRollback() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.rollback());
    }

    @Test
    public void testSetReadOnly() throws SQLException {

        abstractConnection.setReadOnly(true);

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.setReadOnly(false));
    }

    @Test
    public void testGetReadOnly() throws SQLException {

        assertTrue(abstractConnection.isReadOnly());
    }

    @Test
    public void testSetCatalog() throws SQLException {

        abstractConnection.setCatalog("foobar");
        assertNull(abstractConnection.getCatalog());

        abstractConnection.setCatalog(null);
        assertNull(abstractConnection.getCatalog());

    }

    @Test
    public void testGetCatalog() throws SQLException {

        assertNull(abstractConnection.getCatalog());
    }

    @Test
    public void testSetTransactionIsolation() throws SQLException {

        abstractConnection.setTransactionIsolation(Connection.TRANSACTION_NONE);

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED));

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED));

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ));

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE));
    }

    @Test
    public void testGetTransactionIsolation() throws SQLException {

        assertEquals(abstractConnection.getTransactionIsolation(), Connection.TRANSACTION_NONE);
    }

    @Test
    public void testGetWarnings() throws SQLException {

        assertNull(abstractConnection.getWarnings());
    }

    @Test
    public void testClearWarnings() throws SQLException {

        abstractConnection.clearWarnings();
    }

    @Test
    public void testCreateStatementIntInt() throws SQLException {

        try (Statement statement = abstractConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY)) {

            assertNotNull(statement);
        }

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY));

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection
                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));
    }
    
    // TODO: add tests for additional methods

}
