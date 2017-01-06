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

package com.github.dyna4jdbc.internal.common.util.collection;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static com.github.dyna4jdbc.internal.common.util.collection.CollectionTestHelper.assertIteratorReturnsValues;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * @author Peter G. Horvath
 */
public class ListIndexIterableTest {

    private static final List<List<String>> SAME_LENGTH_TEST_DATA = unmodifiableList(asList(
            asList("a", "b", "c"),
            asList("d", "e", "f"),
            asList("g", "h", "i") ));

    private static final List<List<String>> VARIABLE_LENGTH_TEST_DATA = unmodifiableList(asList(
            asList("a", "b"),
            asList("c", "d", "e"),
            asList("f") ));


    @Test(expectedExceptions = NullPointerException.class)
    public void testNullListThrowsNullPointerException() {

        new ListIndexIterable<String>(null, 0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeIndexThrowsIllegalArgumentException() {

        new ListIndexIterable<String>(Collections.emptyList(), -1);
    }


    @Test
    public void testEmptyListHandling() {

        ListIndexIterable<String> iterable = new ListIndexIterable<>(Collections.emptyList(), 0);

        assertIteratorReturnsValues(iterable.iterator(), new Object[0]);
        assertIteratorReturnsValues(iterable.iterator(), new Object[0]);
    }

    @Test
    public void testItemsOfSameLengthListReturnedProperlyMultipleTimes() {

        ListIndexIterable<String> iterableOne = new ListIndexIterable<>(SAME_LENGTH_TEST_DATA, 0);

        assertIteratorReturnsValues(iterableOne.iterator(), "a", "d", "g");
        assertIteratorReturnsValues(iterableOne.iterator(), "a", "d", "g");

        ListIndexIterable<String> iterableTwo = new ListIndexIterable<>(SAME_LENGTH_TEST_DATA, 1);

        assertIteratorReturnsValues(iterableTwo.iterator(), "b", "e", "h");
        assertIteratorReturnsValues(iterableTwo.iterator(), "b", "e", "h");

        ListIndexIterable<String> iterableThree = new ListIndexIterable<>(SAME_LENGTH_TEST_DATA, 2);

        assertIteratorReturnsValues(iterableThree.iterator(), "c", "f", "i");
        assertIteratorReturnsValues(iterableThree.iterator(), "c", "f", "i");
    }

    @Test
    public void testOutIndexingReturnsNulls() {

        ListIndexIterable<String> iterableOne = new ListIndexIterable<>(SAME_LENGTH_TEST_DATA, 10);

        assertIteratorReturnsValues(iterableOne.iterator(), null, null, null);
        assertIteratorReturnsValues(iterableOne.iterator(), null, null, null);
    }

    @Test
    public void testItemsOfDifferentLengthListReturnedProperlyMultipleTimes() {

        ListIndexIterable<String> iterableOne = new ListIndexIterable<>(VARIABLE_LENGTH_TEST_DATA, 0);

        assertIteratorReturnsValues(iterableOne.iterator(), "a", "c", "f");
        assertIteratorReturnsValues(iterableOne.iterator(), "a", "c", "f");

        ListIndexIterable<String> iterableTwo = new ListIndexIterable<>(VARIABLE_LENGTH_TEST_DATA, 1);

        assertIteratorReturnsValues(iterableTwo.iterator(), "b", "d", null);
        assertIteratorReturnsValues(iterableTwo.iterator(), "b", "d", null);

        ListIndexIterable<String> iterableThree = new ListIndexIterable<>(VARIABLE_LENGTH_TEST_DATA, 2);

        assertIteratorReturnsValues(iterableThree.iterator(), null, "e", null);
        assertIteratorReturnsValues(iterableThree.iterator(), null, "e", null);
    }



}
