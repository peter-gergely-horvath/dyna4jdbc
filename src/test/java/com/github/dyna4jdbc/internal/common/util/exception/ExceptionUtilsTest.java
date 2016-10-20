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
 * @author Peter G. Horvath
 */
public class ExceptionUtilsTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetRootCauseMessageNullArgumentHandling() {

        ExceptionUtils.getRootCauseMessage(null);
    }

    @Test
    public void testGetRootCauseMessageSingleExceptionMessageExtraction() {

        String message = ExceptionUtils.getRootCauseMessage(new SQLException("foobar"));

        assertEquals(message, "foobar");
    }

    @Test
    public void testGetRootCauseMessageRootExceptionMessageExtraction() {

        String rootMessage = "foobar";

        Exception exception = new Exception("foo", new Exception("bar", new Exception(rootMessage)));


        String message = ExceptionUtils.getRootCauseMessage(exception);

        assertEquals(message, rootMessage);
    }

    @Test
    public void testGetRootCauseMessageNullRootExceptionMessageExtraction() {

        Exception exception = new Exception("foo", new Exception("bar", new Exception()));


        String message = ExceptionUtils.getRootCauseMessage(exception);

        assertNull(message);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetRootCauseNullArgumentHandling() {

        ExceptionUtils.getRootCause(null);
    }

    @Test
    public void testGetRootCauseSingleExceptionExtraction() {

        SQLException expectedRootCause = new SQLException("foobar");

        Throwable actualRootCause = ExceptionUtils.getRootCause(expectedRootCause);

        assertEquals(actualRootCause, expectedRootCause);
    }

    @Test
    public void testGetRootCauseRootExceptionExtraction() {

        Exception expectedRootCause = new Exception("foobar");

        Exception exception = new Exception("foo", new Exception("bar", expectedRootCause));


        Throwable actualRootCause = ExceptionUtils.getRootCause(exception);

        assertEquals(actualRootCause, expectedRootCause);
    }

    @Test
    public void testGetRootCauseNullRootExceptionExtraction() {

        Exception expectedRootCause = new Exception(null, null);

        Exception exception = new Exception("foo", new Exception("bar", expectedRootCause));


        Throwable actualRootCause = ExceptionUtils.getRootCause(exception);

        assertEquals(actualRootCause, expectedRootCause);
    }
}
