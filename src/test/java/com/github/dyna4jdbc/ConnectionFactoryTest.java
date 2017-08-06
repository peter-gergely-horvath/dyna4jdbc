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

 
package com.github.dyna4jdbc;

import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.assertEquals;

/**
 * @author Peter G. Horvath
 */
public class ConnectionFactoryTest {


    @Test
    public void testConnectionFactorySplitsConfigurationCorrectly() throws SQLException {

        Properties connectionProperties = new Properties();

        ConnectionFactory mockConnectionFactory = new ConnectionFactory() {
            @Override
            protected Connection newConnection(String connectionTypeName, String config, Properties properties) throws Exception {

                assertEquals(connectionTypeName, "foo");

                assertEquals(config, "bar:baz");

                assertEquals(properties, properties);


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
            protected Connection newConnection(String connectionTypeName, String config, Properties properties) throws Exception {

                assertEquals(connectionTypeName, "foo");

                assertEquals(config, null);

                assertEquals(properties, properties);


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
            protected Connection newConnection(String connectionTypeName, String config, Properties properties) throws Exception {

                assertEquals(connectionTypeName, "foo");

                assertEquals(config, "");

                assertEquals(properties, properties);


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
            protected Connection newConnection(String connectionTypeName, String config, Properties properties) throws Exception {

                assertEquals(connectionTypeName, "foobar");

                assertEquals(config, "");

                assertEquals(properties, properties);

                return null;
            }
        };

        mockConnectionFactory.newConnection("FooBar:", connectionProperties);
    }


}
