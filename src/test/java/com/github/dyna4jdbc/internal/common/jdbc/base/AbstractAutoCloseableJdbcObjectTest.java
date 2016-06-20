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
import com.github.dyna4jdbc.internal.RuntimeDyna4JdbcException;

import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;

import static com.github.dyna4jdbc.CommonTestUtils.*;

/**
 * @author Peter Horvath
 */
public class AbstractAutoCloseableJdbcObjectTest {

    /**
     * A minimalistic implementation of {@code AbstractAutoCloseableJdbcObject},
     * which allows us to detect if the an adaopted resource is closed properly:
     * the {@code Callable} supplied as constructor argument can be a mock.
     */
    private class AutoCloseableJdbcObject extends AbstractAutoCloseableJdbcObject {

        AutoCloseableJdbcObject(Callable<Void> closeInternalCallable) throws SQLException {
            super(null);

            registerAsChild(() ->
                    closeInternalCallable.call() );
        }
    }

    private Callable<Void> closeMock;

    private AbstractAutoCloseableJdbcObject closeableJdbcObject;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void beforeMethod() throws SQLException {

        closeMock = createMock(Callable.class);

        closeableJdbcObject = new AutoCloseableJdbcObject(closeMock);
    }



    @AfterMethod
    public void afterMethod() {
        verify(closeMock);
    }

    private void expectAdoptedResourceCloseIsCalled() throws Exception {
        expect(closeMock.call()).andReturn(null).once();
        replay(closeMock);
    }

    private void expectAdoptedResourceCloseIsCalledAndThrow(Throwable throwable) throws Exception {
        expect(closeMock.call()).andThrow(throwable).once();
        replay(closeMock);
    }

    @Test
    public void testCloseCallsCloseInternal() throws Exception {

        expectAdoptedResourceCloseIsCalled();

        closeableJdbcObject.close();
    }

    @Test
    public void testAdoptedResourceCloseIsCalledOnceIfCloseIsCalledMultipleTimes() throws Exception {

        expectAdoptedResourceCloseIsCalled();

        closeableJdbcObject.close();
        closeableJdbcObject.close();
        closeableJdbcObject.close();
    }
    
    @Test
    public void testRegisteringNullChildThrowsException() throws Exception {

        assertThrowsExceptionAndMessageContains(
                RuntimeDyna4JdbcException.class, 
                JDBCError.DRIVER_BUG_UNEXPECTED_STATE.name().toString(), 
                () -> closeableJdbcObject.registerAsChild(null));
        
        expectAdoptedResourceCloseIsCalled();

        closeableJdbcObject.close();
    }

    @Test
    public void testRegisteringChildOnClosedObjectThrowsException() throws Exception {

        expectAdoptedResourceCloseIsCalled();

        AutoCloseable childObject = createStrictMock(AutoCloseable.class);
        replay(childObject);

        closeableJdbcObject.close();

        try {

            closeableJdbcObject.registerAsChild(childObject);

            fail("Should have thrown an exception");

        } catch (RuntimeException ex) {

            String message = ex.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.DRIVER_BUG_UNEXPECTED_STATE.name().toString()));
        }

        verify(childObject);

    }

    @Test
    public void testChildrenAreClosedOnceIfCloseIsCalledMultipleTimes() throws Exception {

        expectAdoptedResourceCloseIsCalled();


        AutoCloseable childOne = createStrictMock(AutoCloseable.class);
        childOne.close();
        expectLastCall().once();

        AutoCloseable childTwo = createStrictMock(AutoCloseable.class);
        childTwo.close();
        expectLastCall().once();

        AutoCloseable childThree = createStrictMock(AutoCloseable.class);
        childThree.close();
        expectLastCall().once();

        closeableJdbcObject.registerAsChild(childOne);
        closeableJdbcObject.registerAsChild(childTwo);
        closeableJdbcObject.registerAsChild(childThree);

        replay(childOne, childTwo, childThree);

        closeableJdbcObject.close();
        closeableJdbcObject.close();
        closeableJdbcObject.close();
        closeableJdbcObject.close();
        closeableJdbcObject.close();

        verify(childOne, childTwo, childThree);

    }

    @Test
    public void testAdoptedResourceCloseExceptionIsNotSwallowed() throws Exception {

        expectAdoptedResourceCloseIsCalledAndThrow(new SQLException("Message from adopted resource"));

        try {

            closeableJdbcObject.close();

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {
            String rootCauseMessage = ExceptionUtils.getRootCauseMessage(sqlEx);
            assertEquals(rootCauseMessage, "Message from adopted resource");
        }

    }

    @Test
    public void testChildrenAreClosedIfAdoptedResourceThrowsException() throws Exception {

        expectAdoptedResourceCloseIsCalledAndThrow(new SQLException("Adapted resource close throws exception"));


        AutoCloseable childOne = createStrictMock(AutoCloseable.class);
        childOne.close();
        expectLastCall().once();

        AutoCloseable childTwo = createStrictMock(AutoCloseable.class);
        childTwo.close();
        expectLastCall().once();

        AutoCloseable childThree = createStrictMock(AutoCloseable.class);
        childThree.close();
        expectLastCall().once();

        replay(childOne, childTwo, childThree);

        closeableJdbcObject.registerAsChild(childOne);
        closeableJdbcObject.registerAsChild(childTwo);
        closeableJdbcObject.registerAsChild(childThree);

        try {

            closeableJdbcObject.close();

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {
            String rootCauseMessage = ExceptionUtils.getRootCauseMessage(sqlEx);
            assertEquals(rootCauseMessage, "Adapted resource close throws exception");
        }

        verify(childOne, childTwo, childThree);

    }

    @Test
    public void testChildrenAreClosedIfAdoptedResourceCloseAndAChildThrowsException() throws Exception {

        expectAdoptedResourceCloseIsCalledAndThrow(new SQLException("Adopted resource close() throws exception"));


        AutoCloseable childOne = createStrictMock(AutoCloseable.class);
        childOne.close();
        expectLastCall().once();

        AutoCloseable childTwo = createStrictMock(AutoCloseable.class);
        childTwo.close();
        expectLastCall().andThrow(new SQLException("childTwo.close() throws exception")).once();

        AutoCloseable childThree = createStrictMock(AutoCloseable.class);
        childThree.close();
        expectLastCall().once();

        replay(childOne, childTwo, childThree);

        closeableJdbcObject.registerAsChild(childOne);
        closeableJdbcObject.registerAsChild(childTwo);
        closeableJdbcObject.registerAsChild(childThree);

        try {

            closeableJdbcObject.close();

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.CLOSE_FAILED.name().toString()));

            assertStrackTraceContainsString(sqlEx, "Adopted resource close() throws exception");
            assertStrackTraceContainsString(sqlEx, "childTwo.close() throws exception");
        }

        verify(childOne, childTwo, childThree);

    }

    @Test
    public void testChildrenAreClosedIfAdoptedResourceCloseAndMultipleChildrenThrowException() throws Exception {

        expectAdoptedResourceCloseIsCalledAndThrow(new SQLException("Adopted resource close() throws exception"));


        AutoCloseable childOne = createStrictMock(AutoCloseable.class);
        childOne.close();
        expectLastCall().andThrow(new SQLException("childOne.close() throws exception")).once();

        AutoCloseable childTwo = createStrictMock(AutoCloseable.class);
        childTwo.close();
        expectLastCall().andThrow(new SQLException("childTwo.close() throws exception")).once();

        AutoCloseable childThree = createStrictMock(AutoCloseable.class);
        childThree.close();
        expectLastCall().andThrow(new SQLException("childThree.close() throws exception")).once();

        replay(childOne, childTwo, childThree);

        closeableJdbcObject.registerAsChild(childOne);
        closeableJdbcObject.registerAsChild(childTwo);
        closeableJdbcObject.registerAsChild(childThree);

        try {

            closeableJdbcObject.close();

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {

            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.CLOSE_FAILED.name().toString()));

            assertStrackTraceContainsString(sqlEx, "Adopted resource close() throws exception");
            assertStrackTraceContainsString(sqlEx, "childOne.close() throws exception");
            assertStrackTraceContainsString(sqlEx, "childTwo.close() throws exception");
            assertStrackTraceContainsString(sqlEx, "childThree.close() throws exception");
        }

        verify(childOne, childTwo, childThree);

    }

    @Test
    public void testChildrenAreClosedIfAChildThrowsException() throws Exception {

        expectAdoptedResourceCloseIsCalled();


        AutoCloseable childOne = createStrictMock(AutoCloseable.class);
        childOne.close();
        expectLastCall().once();

        AutoCloseable childTwo = createStrictMock(AutoCloseable.class);
        childTwo.close();
        expectLastCall().andThrow(new SQLException("childTwo.close() throws exception")).once();

        AutoCloseable childThree = createStrictMock(AutoCloseable.class);
        childThree.close();
        expectLastCall().once();

        replay(childOne, childTwo, childThree);

        closeableJdbcObject.registerAsChild(childOne);
        closeableJdbcObject.registerAsChild(childTwo);
        closeableJdbcObject.registerAsChild(childThree);

        try {

            closeableJdbcObject.close();

            fail("Should have thrown an exception");

        } catch (SQLException sqlEx) {
            String message = sqlEx.getMessage();
            assertNotNull(message);
            assertTrue(message.contains(JDBCError.CLOSE_FAILED.name().toString()));

            assertStrackTraceContainsString(sqlEx, "childTwo.close() throws exception");
        }

        verify(childOne, childTwo, childThree);

    }

    private static void assertStrackTraceContainsString(Throwable throwable, String stringToSearchFor) {

        StringWriter stackTraceWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTraceWriter));
        String stackTraceString = stackTraceWriter.toString();

        assertTrue(stackTraceString.contains(stringToSearchFor),
                String.format("Stack trace does not contain the expected string '%s'", stringToSearchFor));

    }
}
