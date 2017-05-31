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

 
package com.github.dyna4jdbc.internal.common.jdbc.generic;


import com.github.dyna4jdbc.internal.common.datamodel.DataTable;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;

import static org.easymock.EasyMock.*;

public class DataTableAdapterResultSetTest {


    private Statement mockStatement;
    private ColumnHandlerFactory columnHandlerFactory;

    @BeforeMethod
    public void beforeMethod() throws MisconfigurationException, SQLException {

        Configuration configuration = DefaultConfigurationFactory.getInstance()
                .newConfigurationFromParameters("", new Properties());

        columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(configuration);

        mockStatement = createStrictMock(Statement.class);
        expect(mockStatement.getMaxRows()).andReturn(0);
        replay(mockStatement);

    }


    @AfterMethod
    public void afterMethod() {
        verify(mockStatement);
    }


    @Test(expectedExceptions = SQLFeatureNotSupportedException.class)
    public void testGetCursorName() throws SQLException {

        DataTableAdapterResultSet resultSet =
                new DataTableAdapterResultSet(mockStatement, new DataTable(), columnHandlerFactory);

        resultSet.getCursorName();

    }

    @Test
    public void testAutoDetected() throws SQLException {

        DataTableAdapterResultSet resultSet =
                new DataTableAdapterResultSet(mockStatement, new DataTable(), columnHandlerFactory);

        ResultSetMetaData metaData = resultSet.getMetaData();

    }

}
