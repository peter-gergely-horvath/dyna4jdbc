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

package com.github.dyna4jdbc.internal.common.util.collection;

import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.testng.Assert.*;

/**
 * @author Peter Horvath
 */
public class AlwaysSkipFirstElementIterableTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullConstructorArgumentThrowsNullpointerException() {

        new AlwaysSkipFirstElementIterable<>(null);
    }

    @Test
    public void testEmptyIterableIsHandlerCorrectly() {

        AlwaysSkipFirstElementIterable<Object> iterable = new AlwaysSkipFirstElementIterable<>(emptyList());

        Iterator<Object> iterator1 = iterable.iterator();

        assertNotNull(iterator1);
        assertFalse(iterator1.hasNext());

        try {

            iterator1.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception

        }

        Iterator<Object> iterator2 = iterable.iterator();

        assertNotNull(iterator2);
        assertFalse(iterator2.hasNext());

        try {

            iterator2.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception
        }
    }


    @Test
    public void testSingleElementIterableHandlerCorrectly() {

        AlwaysSkipFirstElementIterable<Object> iterable = new AlwaysSkipFirstElementIterable<>(asList("foo"));

        Iterator<Object> iterator1 = iterable.iterator();

        assertNotNull(iterator1);
        assertFalse(iterator1.hasNext());

        try {

            iterator1.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception

        }

        Iterator<Object> iterator2 = iterable.iterator();

        assertNotNull(iterator2);
        assertFalse(iterator2.hasNext());

        try {

            iterator2.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception
        }
    }

    @Test
    public void testMultipleElementsIterableHandlerCorrectly() {

        AlwaysSkipFirstElementIterable<Object> iterable = new AlwaysSkipFirstElementIterable<>(asList("foo", "bar"));

        Iterator<Object> iterator1 = iterable.iterator();

        assertNotNull(iterator1);

        assertTrue(iterator1.hasNext());
        assertEquals("bar", iterator1.next());

        assertFalse(iterator1.hasNext());

        try {

            iterator1.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception

        }

        Iterator<Object> iterator2 = iterable.iterator();

        assertTrue(iterator2.hasNext());
        assertEquals("bar", iterator2.next());

        assertNotNull(iterator2);
        assertFalse(iterator2.hasNext());

        try {

            iterator2.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception
        }
    }

}
