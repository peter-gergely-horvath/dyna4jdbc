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

package com.github.dyna4jdbc.internal.common.datamodel;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.github.dyna4jdbc.internal.common.util.collection.CollectionTestHelper.assertIteratorReturnsValues;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static org.testng.Assert.*;

/**
 * @author Peter Horvath
 */
public class DataTableTest {

    private static final List<List<String>> TEST_DATA = unmodifiableList(asList(
            asList("a", "b"),
            asList("c", "d", "e"),
            asList("f") ));

    private DataTable dataTable;

    @BeforeMethod
    public void beforeMethod() {
        dataTable = new DataTable();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddingNullRow() {

        dataTable.appendRow(null);
    }

    @Test
    public void testEmptryList() {

        dataTable.appendRow(emptyList());
        dataTable.appendRow(emptyList());
        dataTable.appendRow(emptyList());

        assertEquals(dataTable.getColumnCount(), 0);

        Iterator<DataColumn> iterator = dataTable.columnIterable().iterator();

        assertFalse(iterator.hasNext());

        try {
            iterator.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception
        }
    }

    @Test
    public void testClear() {

        for (List<String> row : TEST_DATA) {
            dataTable.appendRow(new LinkedList<>(row));
        }

        assertFalse(dataTable.isEmpty());
        assertEquals(dataTable.getRows().size(), TEST_DATA.size());

        dataTable.clear();

        assertTrue(dataTable.isEmpty());
        assertEquals(dataTable.getRows().size(), 0);
    }



        @Test
    public void testAppendedRow() {

        assertTrue(dataTable.isEmpty());

        for(List<String> row : TEST_DATA) {

            List<String> appendedRow = dataTable.appendRow(row);

            assertEquals(row, appendedRow);
        }

        assertFalse(dataTable.isEmpty());

        assertEquals(dataTable.getRows().size(), TEST_DATA.size());

    }

    @Test
    public void testAppendedRowContent() {

        for(List<String> row : TEST_DATA) {

            dataTable.appendRow(row);
        }

        assertIteratorReturnsValues(dataTable.getRows().iterator(), TEST_DATA.toArray());
    }

    @Test
    public void testColumnCount() {

        for(List<String> row : TEST_DATA) {

            dataTable.appendRow(row);
        }

        Integer maxRowSize = TEST_DATA.stream().map(row -> row.size()).max(Integer::compare).get();

        assertEquals(dataTable.getColumnCount(), maxRowSize.intValue());
    }

    @Test
    public void testColumnData() {

        for(List<String> row : TEST_DATA) {

            dataTable.appendRow(row);
        }

        Iterator<DataColumn> dataColumnIterator = dataTable.columnIterable().iterator();

        DataColumn dataColumn;

        dataColumn = dataColumnIterator.next();

        assertIteratorReturnsValues(dataColumn.iterator(), "a", "c", "f");
        assertIteratorReturnsValues(dataColumn.iterator(), "a", "c", "f");

        dataColumn = dataColumnIterator.next();

        assertIteratorReturnsValues(dataColumn.iterator(), "b", "d", null);
        assertIteratorReturnsValues(dataColumn.iterator(), "b", "d", null);

        dataColumn = dataColumnIterator.next();

        assertIteratorReturnsValues(dataColumn.iterator(), null, "e", null);
        assertIteratorReturnsValues(dataColumn.iterator(), null, "e", null);

        assertFalse(dataColumnIterator.hasNext());

        try {
            dataColumnIterator.next();

            fail("Should have thrown an exception");
        } catch (NoSuchElementException nse) {
            // expected exception
        }


    }
}
