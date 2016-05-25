package com.github.dyna4jdbc.integrationtests;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.Test;

import java.sql.*;

import static org.testng.Assert.*;

public class BasicScriptEngineTest {

    @Test
    public void testUnspecifiedScriptEngineThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.toString()), message);
        }
    }

    @Test
    public void testInvalidScriptEngineThrowsSQLException() {

        try {

            DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:foobar");

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.LOADING_SCRIPTENGINE_FAILED.toString()), message);
        }
    }
}


