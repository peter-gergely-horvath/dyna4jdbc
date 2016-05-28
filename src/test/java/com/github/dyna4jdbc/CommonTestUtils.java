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
    public static interface VoidCallable {
        void call() throws Exception;
    };
    
    public static void assertThrowsSQLExceptionWithFunctionNotSupportedMessage(VoidCallable callable) {
        assertThrowsSQLExceptionAndMessageContains(JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.name(), 
                new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                
                callable.call();
                
                return null;
            }
        });
    }
    
    public static void assertThrowsSQLExceptionWithJDBCError(JDBCError jdbcError, VoidCallable callable) {
        assertThrowsSQLExceptionAndMessageContains(jdbcError.name(), 
                new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                
                callable.call();
                
                return null;
            }
        });
    }

    public static void assertThrowsSQLExceptionWithFunctionNotSupportedMessage(Callable<?> callable) {
        assertThrowsSQLExceptionAndMessageContains(JDBCError.JDBC_FUNCTION_NOT_SUPPORTED.name(), callable);
    }
    
    public static void assertThrowsSQLExceptionAndMessageContains(
            String expectedMessageContent, Callable<?> callable) {

        assertThrowsExceptionAndMessageContains(
                java.sql.SQLException.class, 
                expectedMessageContent, 
                callable);
    }

    public static void assertThrowsExceptionAndMessageContains(Class<? extends Exception> expectedExceptionClazz,
            String expectedMessageContent, Callable<?> callable) {

        try {

            callable.call();

            fail("Should have thrown " + expectedExceptionClazz);
            
        } catch (Exception ex) {

            assertTrue(expectedExceptionClazz.isAssignableFrom(ex.getClass()),
                    String.format("Expected to be '%s' (or a subclass), but was '%s'.", 
                            expectedExceptionClazz, ex));

            String message = ex.getMessage();

            assertTrue(message.contains(expectedMessageContent), String.format(
                    "Invalid message: expected to contain '%s', but was '%s'", expectedMessageContent, message));
        }
    }
}
