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
            assertTrue(message.contains(JDBCError.INVALID_CONFIGURATION.name().toString()), message);
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
            assertTrue(message.contains(JDBCError.LOADING_SCRIPTENGINE_FAILED.name().toString()), message);
        }
    }

    @Test
    public void testDuplicatedHeaderThrowsSQLException() {

        try(Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript")) {

            try(Statement statement = connection.createStatement()) {

                statement.executeQuery("print(\"FOOBAR::\tFOOBAR::\");\n print(\"FOO\tBAR\");");

                fail("Should have thrown an exception");
            }
        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.DUPLICATED_HEADER_NAME.name().toString()), message);
            assertTrue(message.contains("FOOBAR"), message);
        }
    }
}


