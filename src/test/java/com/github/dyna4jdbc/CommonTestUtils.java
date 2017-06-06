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

 
package com.github.dyna4jdbc;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.Callable;

import com.github.dyna4jdbc.internal.JDBCError;

public class CommonTestUtils {

    private CommonTestUtils() {
        // static utility class
    }

    @FunctionalInterface
    public interface VoidCallable {
        void call() throws Exception;
    }

    public static void assertThrowsSQLExceptionWithFunctionNotSupportedMessage(VoidCallable callable) {
        assertThrowsSQLExceptionAndMessageContains(JDBCError.JDBC_FEATURE_NOT_SUPPORTED.name(),
                adaptToCallable(callable));
    }

    public static void assertThrowsSQLExceptionWithJDBCError(JDBCError jdbcError, VoidCallable callable) {
        assertThrowsSQLExceptionAndMessageContains(jdbcError.name(), new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                callable.call();

                return null;
            }
        });
    }

    public static void assertThrowsSQLExceptionWithFunctionNotSupportedMessage(Callable<?> callable) {
        assertThrowsSQLExceptionAndMessageContains(JDBCError.JDBC_FEATURE_NOT_SUPPORTED.name(), callable);
    }
    
    public static void assertThrowsSQLExceptionAndMessageContains(String expectedMessageContent, Callable<?> callable) {

        assertThrowsExceptionAndMessageContains(java.sql.SQLException.class, expectedMessageContent, callable);
    }

    public static void assertThrowsExceptionAndMessageContains(Class<? extends Exception> expectedExceptionClazz,
            String expectedMessageContent, VoidCallable callable) {

        assertThrowsExceptionAndMessageContains(expectedExceptionClazz, expectedMessageContent,
                adaptToCallable(callable));
    }

    public static void assertThrowsExceptionAndMessageContains(Class<? extends Exception> expectedExceptionClazz,
            String expectedMessageContent, Callable<?> callable) {

        try {

            callable.call();

            fail("Should have thrown " + expectedExceptionClazz);

        } catch (Exception ex) {

            assertTrue(expectedExceptionClazz.isAssignableFrom(ex.getClass()),
                    String.format("Expected to be '%s' (or a subclass), but was '%s'.", expectedExceptionClazz, ex));

            String message = ex.getMessage();

            assertTrue(message.contains(expectedMessageContent), String.format(
                    "Invalid message: expected to contain '%s', but was '%s'", expectedMessageContent, message));
        }
    }

    private static Callable<Void> adaptToCallable(VoidCallable callable) {
        return new Callable<Void>() {

            @Override
            public Void call() throws Exception {

                callable.call();

                return null;
            }
        };
    }
}
