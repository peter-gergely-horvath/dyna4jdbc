package com.github.dyna4jdbc.internal.common.util.sqlwarning;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.RuntimeDyna4JdbcException;

import static com.github.dyna4jdbc.CommonTestUtils.*;

import java.sql.SQLException;
import java.sql.SQLWarning;

public class SQLWarningContainerTest {
    
    private SQLWarningContainer sqlWarningContainer;
    
    
    @BeforeMethod
    public void beforeTest() {
        this.sqlWarningContainer = new SQLWarningContainer();
    }
    
    @Test
    public void testAddingNullThrowsNullpointerException() {
        
        assertThrowsExceptionAndMessageContains(RuntimeDyna4JdbcException.class, 
                JDBCError.DRIVER_BUG_UNEXPECTED_STATE.name(), 
                () -> sqlWarningContainer.addSQLWarning(null) );
    }
    
    @Test
    public void testCallingClearOnEmptySQLWarningContainerSucceeds() {
        
        SQLWarning warnings;
        
        warnings = sqlWarningContainer.getWarnings();
        assertNull(warnings);
        
        
        sqlWarningContainer.clearWarnings();
        
        
        warnings = sqlWarningContainer.getWarnings();
        assertNull(warnings);
    }
    
    @Test
    public void testSingleMultipleSQLWarnings() throws SQLException {
        
        SQLWarning warnings;
        
        warnings = sqlWarningContainer.getWarnings();
        
        assertNull(warnings);
        
        
        sqlWarningContainer.addSQLWarning(new SQLWarning("foo"));
        
        warnings = sqlWarningContainer.getWarnings();
        
        assertNotNull(warnings);
        assertEquals(warnings.getMessage(), "foo");
        
        assertNull(warnings.getNextWarning());
        
        sqlWarningContainer.clearWarnings();
        
        warnings = sqlWarningContainer.getWarnings();
        
        assertNull(warnings);
    }
    
    @Test
    public void testMultipleSQLWarnings() throws SQLException {
        
        SQLWarning warnings;
        
        warnings = sqlWarningContainer.getWarnings();
        
        assertNull(warnings);
        
        
        sqlWarningContainer.addSQLWarning(new SQLWarning("foo"));
        sqlWarningContainer.addSQLWarning(new SQLWarning("bar"));
        sqlWarningContainer.addSQLWarning(new SQLWarning("baz"));
        
        warnings = sqlWarningContainer.getWarnings();
        
        assertNotNull(warnings);
        assertEquals(warnings.getMessage(), "foo");
        
        warnings = warnings.getNextWarning();
        assertNotNull(warnings);
        assertEquals(warnings.getMessage(), "bar");
        
        warnings = warnings.getNextWarning();
        assertNotNull(warnings);
        assertEquals(warnings.getMessage(), "baz");
        
        assertNull(warnings.getNextWarning());
        
        sqlWarningContainer.clearWarnings();
        
        warnings = sqlWarningContainer.getWarnings();
        
        assertNull(warnings);
    }
}
