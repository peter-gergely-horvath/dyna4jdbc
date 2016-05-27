package com.github.dyna4jdbc.internal.common.util.collection;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Peter Horvath
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
