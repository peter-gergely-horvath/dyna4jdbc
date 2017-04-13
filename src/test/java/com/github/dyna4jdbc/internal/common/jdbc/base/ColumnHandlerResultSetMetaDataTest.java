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

 
package com.github.dyna4jdbc.internal.common.jdbc.base;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ColumnHandlerResultSetMetaDataTest {

    private ColumnHandlerResultSetMetaData columnHandlerResultSetMetaData;
    private List mockColumnHandlerList;

    @BeforeMethod
    public void beforeMethod() {

        mockColumnHandlerList = createStrictMock(List.class);

        this.columnHandlerResultSetMetaData = new ColumnHandlerResultSetMetaData(mockColumnHandlerList);
    }

    @AfterMethod
    public void afterMethod() {
        verify(mockColumnHandlerList);
    }

    private void expectColumnHandlerListNotUsed() {
        replay(mockColumnHandlerList);
    }

    @Test
    public void testIsAutoIncrement() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertFalse(columnHandlerResultSetMetaData.isAutoIncrement(0));
    }

    @Test
    public void testIsCaseSensitive() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertTrue(columnHandlerResultSetMetaData.isCaseSensitive(0));
    }

    @Test
    public void testIsSearchable() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertFalse(columnHandlerResultSetMetaData.isSearchable(0));
    }

    @Test
    public void testGetSchemaName() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertEquals("", columnHandlerResultSetMetaData.getSchemaName(0));
    }

    @Test
    public void testGetTableName() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertEquals("", columnHandlerResultSetMetaData.getTableName(0));
    }

    @Test
    public void testGetCatalogName() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertEquals("", columnHandlerResultSetMetaData.getCatalogName(0));
    }

    @Test
    public void testIsReadOnly() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertTrue(columnHandlerResultSetMetaData.isReadOnly(0));
    }

    @Test
    public void testIsWritable() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertFalse(columnHandlerResultSetMetaData.isWritable(0));
    }

    @Test
    public void testIsDefinitelyWritable() throws SQLException {

        expectColumnHandlerListNotUsed();

        assertFalse(columnHandlerResultSetMetaData.isDefinitelyWritable(0));
    }
}
