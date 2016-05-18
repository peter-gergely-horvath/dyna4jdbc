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

package com.github.dyna4jdbc.internal.common.jdbc.base;

import com.github.dyna4jdbc.internal.JDBCError;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static org.testng.Assert.*;


/**
 * @author Peter Horvath
 */
public class AbstractWrapperTest {

    private AbstractWrapper abstractWrapper;

    /**
     * A minimalistic subclass of {@code AbstractWrapper},
     * which allows us to test the logic in that abstract class.
     */
    private class ConcreteWrapper extends AbstractWrapper {
        // no additional feature is implemented
    }


    @BeforeMethod
    public void beforeMethod() {
        this.abstractWrapper = new ConcreteWrapper();
    }

    @Test
    public void testIsWrapper() throws SQLException {
        assertFalse(abstractWrapper.isWrapperFor(java.sql.ResultSet.class));
    }

    @Test
    public void testUnwrap() throws SQLException {

        try {

            abstractWrapper.unwrap(java.sql.ResultSet.class);

            fail("Should throw an exception");

        } catch (SQLException sqlex) {

            String message = sqlex.getMessage();

            assertTrue(message.contains(JDBCError.CANNOT_UNWARP_OBJECT.name()),
                    String.format("Invalid message: '%s'", message));
        }
    }
}
