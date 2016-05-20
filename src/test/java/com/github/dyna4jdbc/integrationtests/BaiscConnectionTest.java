package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.testng.Assert.*;

public class BaiscConnectionTest {

    @Test
    public void testMalformedUrlThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);

            assertTrue(message.contains(JDBCError.CONNECT_FAILED_INVALID_URL.toString()), message);
        }
    }

    @Test
    public void testEmptyConnectionDetailsThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);

            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.toString()), message);
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

            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.toString()), message);
        }
    }
}
