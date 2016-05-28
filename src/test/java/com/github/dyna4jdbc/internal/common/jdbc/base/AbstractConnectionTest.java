package com.github.dyna4jdbc.internal.common.jdbc.base;


import static org.testng.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

import scala.collection.immutable.HashMap;

import static com.github.dyna4jdbc.CommonTestUtils.*;

public class AbstractConnectionTest {

    private AbstractConnection abstractConnection;

    @BeforeMethod
    public void beforeMethod() throws SQLException, MisconfigurationException {

        // we use JavaScript ScriptEngineConnection to test AbstractConnection
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
    public void testSetSavepoint() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.setSavepoint());
    }
    
    @Test
    public void testSetSavepointString() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(() -> abstractConnection.setSavepoint("foobar"));
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
    
    @Test
    public void testPrepareStatementStringIntInt() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY));
    }
    
    @Test
    public void testPepareCallStringIntInt() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY));
    }
    
    @Test
    public void testGetTypeMap() throws SQLException {
        
        Map<String, Class<?>> typeMap = abstractConnection.getTypeMap();
        
        assertNotNull(typeMap);
        
        
        try {
            typeMap.put("foo", String.class);
            
            fail("Should have thrown an exception");
            
        } catch(UnsupportedOperationException uoe) {
            // expected exception
        }
    }
    
    @Test
    public void testSetTypeMap() throws SQLException {
        
        Map<String, Class<?>> typeMap = abstractConnection.getTypeMap();
        
        assertFalse(typeMap.containsKey("foobar"));
        
        Map<String, Class<?>> newTypeMap = new java.util.HashMap<>();
        
        newTypeMap.put("foobar", String.class);
        
        abstractConnection.setTypeMap(newTypeMap);
        
        typeMap = abstractConnection.getTypeMap();
        
        assertEquals(typeMap, newTypeMap);
        
        assertTrue(typeMap.containsKey("foobar"));
        
        newTypeMap.remove("foobar");
        
        // changing the outer map is not reflected in the contained one 
        assertTrue(abstractConnection.getTypeMap().containsKey("foobar")); 
        
    }
    
    // TODO: add tests for additional methods

}
