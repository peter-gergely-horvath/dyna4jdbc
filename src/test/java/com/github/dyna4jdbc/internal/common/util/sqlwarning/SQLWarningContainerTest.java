/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
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
