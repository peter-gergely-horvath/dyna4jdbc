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

import static org.testng.Assert.*;

/**
 * @author Peter G. Horvath
 */
public class ArrayUtilsTest {

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullArgument() {
        ArrayUtils.tryGetByIndex(null, 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeIndexValue() {
        ArrayUtils.tryGetByIndex(new String[] { "foo", "and", "bar"}, -1);
    }

    @Test
    public void testEmptyArrayRetrievalWithZeroIndex() {
        String resultString = ArrayUtils.tryGetByIndex(new String[0], 0);

        assertNull(resultString);
    }

    @Test
    public void testEmptyArrayRetrievalWithIntegerMaxValue() {
        String resultString = ArrayUtils.tryGetByIndex(new String[0], Integer.MAX_VALUE);

        assertNull(resultString);
    }

    @Test
    public void testIndexLookupReturnsExpectedValue() {

        String[] testArray = {"Foo", "And", "Bar"};

        int index = 0;
        for (; index<testArray.length; index++) {

            String actual = ArrayUtils.tryGetByIndex(testArray, index);

            assertEquals(actual, testArray[index]);
        }

        final int invalidIndex = index + 1;

        String nonExistingEntry = ArrayUtils.tryGetByIndex(testArray, invalidIndex);

        assertNull(nonExistingEntry);
    }


}
