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

package com.github.dyna4jdbc.internal.common.util.exception;

import org.testng.annotations.Test;

import java.sql.SQLException;


import static org.testng.Assert.*;

/**
 * @author Peter Horvath
 */
public class ExceptionUtilTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgumentHandling() {

        ExceptionUtil.getRootCauseMessage(null);
    }

    @Test
    public void testSingleExceptionMessageExtraction() {

        String message = ExceptionUtil.getRootCauseMessage(new SQLException("foobar"));

        assertEquals(message, "foobar");
    }

    @Test
    public void testRootExceptionMessageExtraction() {

        String rootMessage = "foobar";

        Exception exception = new Exception("foo", new Exception("bar", new Exception(rootMessage)));


        String message = ExceptionUtil.getRootCauseMessage(exception);

        assertEquals(message, rootMessage);
    }

    @Test
    public void testNullRootExceptionMessageExtraction() {

        Exception exception = new Exception("foo", new Exception("bar", new Exception()));


        String message = ExceptionUtil.getRootCauseMessage(exception);

        assertNull(message);
    }



}
