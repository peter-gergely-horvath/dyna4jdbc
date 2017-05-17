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
