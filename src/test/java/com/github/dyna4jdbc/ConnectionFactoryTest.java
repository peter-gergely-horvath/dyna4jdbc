package com.github.dyna4jdbc;

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author Peter Horvath
 */
public class ConnectionFactoryTest {


    @Test
    public void testConnectionFactorySplitsConfigurationCorrectly() throws SQLException {

        Properties connectionProperties = new Properties();

        ConnectionFactory mockConnectionFactory = new ConnectionFactory() {
            @Override
            protected Connection newConnection(String connectionType, String config, Properties info) throws Exception {

                assertEquals(connectionType, "foo");

                assertEquals(config, "bar:baz");

                assertEquals(info, connectionProperties);


                return null;
            }
        };

        mockConnectionFactory.newConnection("foo:bar:baz", connectionProperties);
    }

    @Test
    public void testConnectionFactoryPassesNullWhenColonIsPresent() throws SQLException {

        Properties connectionProperties = new Properties();

        ConnectionFactory mockConnectionFactory = new ConnectionFactory() {
            @Override
            protected Connection newConnection(String connectionType, String config, Properties info) throws Exception {

                assertEquals(connectionType, "foo");

                assertEquals(config, null);

                assertEquals(info, connectionProperties);


                return null;
            }
        };

        mockConnectionFactory.newConnection("foo", connectionProperties);
    }

    @Test
    public void testConnectionFactoryPassesEmptyStringWhenColonIsPresent() throws SQLException {

        Properties connectionProperties = new Properties();

        ConnectionFactory mockConnectionFactory = new ConnectionFactory() {
            @Override
            protected Connection newConnection(String connectionType, String config, Properties info) throws Exception {

                assertEquals(connectionType, "foo");

                assertEquals(config, "");

                assertEquals(info, connectionProperties);


                return null;
            }
        };

        mockConnectionFactory.newConnection("foo:", connectionProperties);
    }

    @Test
    public void testConnectionFactoryConvertsToLowerCase() throws SQLException {

        Properties connectionProperties = new Properties();

        ConnectionFactory mockConnectionFactory = new ConnectionFactory() {
            @Override
            protected Connection newConnection(String connectionType, String config, Properties info) throws Exception {

                assertEquals(connectionType, "foobar");

                assertEquals(config, "");

                assertEquals(info, connectionProperties);

                return null;
            }
        };

        mockConnectionFactory.newConnection("FooBar:", connectionProperties);
    }


}
