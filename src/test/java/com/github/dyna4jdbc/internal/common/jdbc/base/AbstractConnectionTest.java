package com.github.dyna4jdbc.internal.common.jdbc.base;


import static com.github.dyna4jdbc.CommonTestUtils.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

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
    public void testPepareCallStringIntIntInt() throws SQLException {

        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, 
                        ResultSet.CONCUR_UPDATABLE, 
                        ResultSet.HOLD_CURSORS_OVER_COMMIT ));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, 
                        ResultSet.CONCUR_UPDATABLE, 
                        ResultSet.CLOSE_CURSORS_AT_COMMIT ));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, 
                        ResultSet.CONCUR_READ_ONLY, 
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_FORWARD_ONLY, 
                        ResultSet.CONCUR_READ_ONLY, 
                        ResultSet.CLOSE_CURSORS_AT_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE, 
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE, 
                        ResultSet.CLOSE_CURSORS_AT_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, 
                        ResultSet.CONCUR_READ_ONLY, 
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_INSENSITIVE, 
                        ResultSet.CONCUR_READ_ONLY, 
                        ResultSet.CLOSE_CURSORS_AT_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE, 
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE, 
                        ResultSet.CLOSE_CURSORS_AT_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_READ_ONLY, 
                        ResultSet.HOLD_CURSORS_OVER_COMMIT));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareCall(
                        "foobar",
                        ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_READ_ONLY, 
                        ResultSet.CLOSE_CURSORS_AT_COMMIT));
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
    
    @Test
    public void testSetHoldability() throws SQLException {
        
        abstractConnection.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
        assertEquals(abstractConnection.getHoldability(), ResultSet.HOLD_CURSORS_OVER_COMMIT);
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage( () ->
            abstractConnection.setHoldability(ResultSet.CLOSE_CURSORS_AT_COMMIT) );
        
        assertThrowsSQLExceptionWithJDBCError(JDBCError.JDBC_API_USAGE_CALLER_ERROR, () -> 
            abstractConnection.setHoldability( Integer.MAX_VALUE ) );
    }
    
    @Test
    public void testGetHoldability() throws SQLException {
        
        assertEquals(abstractConnection.getHoldability(), ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }
    
    @Test
    public void testPrepareStatementStringInt() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        Statement.RETURN_GENERATED_KEYS) );
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        Statement.NO_GENERATED_KEYS) );
    }
    
    @Test
    public void testPrepareStatementStringIntArray() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        new int[] { 1 } ));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        new int[] { 1 } ));
    }
    
    @Test
    public void testPrepareStatementStringStringArray() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        new String[] { "foobar" } ));
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.prepareStatement(
                        "foobar",
                        new String[] { "foobar" } ));
    }
   
    @Test
    public void testCreateClob() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.createClob() );
    }
    
    @Test
    public void testCreateBlob() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.createBlob() );
    }
    
    @Test
    public void testCreateNClob() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.createNClob() );
    }
    
    @Test
    public void testCreateSQLXML() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                () -> abstractConnection.createSQLXML() );
    }
    
    @Test
    public void testValid() throws SQLException {
        
        assertThrowsSQLExceptionWithJDBCError(JDBCError.JDBC_API_USAGE_CALLER_ERROR,
                ()-> abstractConnection.isValid(-1));
        
        assertTrue(abstractConnection.isValid(0));
        
        abstractConnection.close();
        
        assertFalse(abstractConnection.isValid(0));
    }
    
    @Test
    public void testClientInfo() throws SQLException {
        
        Properties clientInfo = abstractConnection.getClientInfo();
        
        assertTrue(clientInfo.isEmpty());
        
        // mutating the retrieved properties does not
        // no change the state of the internal values
        clientInfo.setProperty("foo", "bar");
        assertTrue(abstractConnection.getClientInfo().isEmpty());
        
        abstractConnection.setClientInfo(clientInfo);
        assertEquals("bar", abstractConnection.getClientInfo("foo"));
        
        clientInfo.remove("foo");
        assertEquals("bar", abstractConnection.getClientInfo("foo"));
        
        assertNull(abstractConnection.getClientInfo("foobar"));
        abstractConnection.setClientInfo("foobar", "baz");
        assertEquals("baz", abstractConnection.getClientInfo("foobar"));
    }
    
    @Test
    public void testCreateArrayOf() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                ()-> abstractConnection.createArrayOf("VARCHAR", new Object[0]));
    
    }
    
    @Test
    public void testCreateStruct() throws SQLException {
        
        assertThrowsSQLExceptionWithFunctionNotSupportedMessage(
                ()-> abstractConnection.createStruct("VARCHAR", new Object[0]));
    }
    
    @Test
    public void testSetSchema() throws SQLException {
        abstractConnection.setSchema("foobar");
        
        assertNull(abstractConnection.getSchema());
    }
    
    @Test
    public void testGetSchema() throws SQLException {
        
        assertNull(abstractConnection.getSchema());
    }
    
    @Test
    public void testNetworkTimeout() throws SQLException {
        
        abstractConnection.setNetworkTimeout(Runnable::run, 0);
        
        assertThrowsSQLExceptionWithJDBCError(JDBCError.JDBC_API_USAGE_CALLER_ERROR, () -> 
            abstractConnection.setNetworkTimeout(Runnable::run, -1) );
        
        abstractConnection.setNetworkTimeout(Runnable::run, 10);
        
        assertEquals(abstractConnection.getNetworkTimeout(), 0);
    }
    
    @Test
    public void testAbortCompleted() throws SQLException {
        
        abstractConnection.abort(Runnable::run); // caller thread: sync operation!
        
        assertTrue(abstractConnection.isClosed());
        
    }
    
    @Test
    public void testAbortRejected() throws SQLException {
        
        Executor rejectAll = new Executor() {
            
            @Override
            public void execute(Runnable command) {
                throw new RejectedExecutionException();
            }
        };
        
        assertThrowsSQLExceptionWithJDBCError(JDBCError.CLOSE_FAILED, () -> 
            abstractConnection.abort(rejectAll) );
        
        assertTrue(abstractConnection.isClosed());
    }
}

