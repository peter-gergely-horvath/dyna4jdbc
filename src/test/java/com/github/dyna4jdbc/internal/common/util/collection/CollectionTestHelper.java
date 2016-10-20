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

import java.util.Iterator;
import java.util.LinkedList;

import static org.testng.Assert.assertEquals;

/**
 * @author Peter G. Horvath
 */
public class CollectionTestHelper {

    public static void assertIteratorReturnsValues(Iterator<?> actualIterator, Object... expectedObjects) {
        LinkedList<Object> actualObjects = new LinkedList<>();

        while (actualIterator.hasNext()) {
            actualObjects.add(actualIterator.next());
        }

        assertEquals(actualObjects.size(), expectedObjects.length,
                "Number of iterator elements does not match number of expected elements:" );


        for(int i=0; i < expectedObjects.length; i++) {

            Object actual = actualObjects.get(i);
            Object expected = expectedObjects[i];

            assertEquals(actual, expected, String.format("At i = %s: ", i));
        }
    }
}
