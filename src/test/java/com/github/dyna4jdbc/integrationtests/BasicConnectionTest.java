package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.DynaDriver;
import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.*;

public class BasicConnectionTest {

    @Test
    public void testConnectAttemptWithInvalidUrlReturnsNull() throws SQLException {

        DynaDriver driver = new com.github.dyna4jdbc.DynaDriver();
        
        Connection connection = driver.connect("jdbc:dyna4jdbc", new Properties());
        assertNull(connection);
    }

    @Test
    public void testEmptyConnectionDetailsThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);

            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.name().toString()), message);
        }
    }

    @Test
    public void testInvalidTypeThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:foobar");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);

            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.name().toString()), message);
        }
    }
}
