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

 
package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import org.testng.annotations.Test;

import java.util.*;

import static com.github.dyna4jdbc.internal.nodejs.jdbc.impl.JavaScriptVariableConverter.convertToString;
import static org.testng.Assert.assertEquals;

/**
 * @author Peter G. Horvath
 */
public class JavaScriptVariableConverterTest {

    @Test
    public void testNull() {

        String stringRepresentation = convertToString(null);
        assertEquals(stringRepresentation, "null");
    }

    @Test
    public void testString() {

        String stringRepresentation = convertToString("Hello World");
        assertEquals(stringRepresentation, "\'Hello World\'");

        stringRepresentation = convertToString("McDonald\\'s");
        assertEquals(stringRepresentation, "'McDonald\\'s'");

        stringRepresentation = convertToString("McDonald's");
        assertEquals(stringRepresentation, "'McDonald\\'s'");
    }

    @Test
    public void testCharacter() {

        String stringRepresentation = convertToString('X');
        assertEquals(stringRepresentation, "'X'");
    }


    @Test
    public void testBoolean() {

        String stringRepresentation = convertToString(true);
        assertEquals(stringRepresentation, "true");

        stringRepresentation = convertToString(false);
        assertEquals(stringRepresentation, "false");
    }

    @Test
    public void testByte() {

        String stringRepresentation = convertToString((byte) 42);
        assertEquals(stringRepresentation, "42");
    }

    @Test
    public void testShort() {

        String stringRepresentation = convertToString((short) 42);
        assertEquals(stringRepresentation, "42");
    }

    @Test
    public void testInteger() {

        String stringRepresentation = convertToString(42);
        assertEquals(stringRepresentation, "42");
    }

    @Test
    public void testLong() {

        String stringRepresentation = convertToString(42L);
        assertEquals(stringRepresentation, "42");
    }

    @Test
    public void testFloat() {

        String stringRepresentation = convertToString(12.34f);
        assertEquals(stringRepresentation, "12.34");
    }

    @Test
    public void testDouble() {

        String stringRepresentation = convertToString(12.34d);
        assertEquals(stringRepresentation, "12.34");
    }

    @Test
    public void testDate() {

        Date date = new Date();
        long time = date.getTime();

        String expectedVariableDefinition = String.format("new Date(%s)", time);

        String stringRepresentation = convertToString(date);
        assertEquals(stringRepresentation, expectedVariableDefinition);
    }

    @Test
    public void testStringArray() {

        String stringRepresentation = convertToString(new Object[]{"foo", "and", "bar"});
        assertEquals(stringRepresentation, "[ 'foo', 'and', 'bar' ]");
    }

    @Test
    public void testNumberArray() {

        String stringRepresentation = convertToString(new Object[]{12.34f, 56, 789});
        assertEquals(stringRepresentation, "[ 12.34, 56, 789 ]");
    }

    @Test
    public void testStringCollection() {

        String stringRepresentation = convertToString(Arrays.asList("foo", "and", "bar"));
        assertEquals(stringRepresentation, "[ 'foo', 'and', 'bar' ]");
    }

    @Test
    public void testNumberCollection() {

        String stringRepresentation = convertToString(Arrays.asList(12.34f, 56, 789));
        assertEquals(stringRepresentation, "[ 12.34, 56, 789 ]");
    }

    @Test
    public void testNestedStringCollection() {

        String stringRepresentation = convertToString(Arrays.asList(
                Arrays.asList("foo", "and", "boo"),
                Arrays.asList(12.34f, 56, 789),
                Collections.emptyList()));
        assertEquals(stringRepresentation, "[ [ 'foo', 'and', 'boo' ], [ 12.34, 56, 789 ], [  ] ]");
    }

    @Test
    public void testSimpleMap() {

        HashMap<String, Object> map = new LinkedHashMap<>();
        map.put("foo", 12.34f);
        map.put("and", 56);
        map.put("bar", 789);

        String stringRepresentation = convertToString(map);
        assertEquals(stringRepresentation, "{ 'foo' : 12.34, 'and' : 56, 'bar' : 789 }");
    }

    @Test
    public void testGenericObject() {

        Object object = new Object();

        String stringRepresentation = convertToString(object);
        assertEquals(stringRepresentation, String.format("'%s'", object.toString()));
    }


}
