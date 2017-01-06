/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
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
