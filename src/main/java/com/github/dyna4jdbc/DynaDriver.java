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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dyna4jdbc.internal.DriverInfo;
import com.github.dyna4jdbc.internal.config.impl.DriverPropertyInfoFactory;

public final class DynaDriver implements java.sql.Driver {

    private static final Logger LOGGER = Logger.getLogger(DynaDriver.class.getName());
    private static final Logger PARENT_LOGGER = Logger.getLogger(DynaDriver.class.getPackage().getName());

    static {
        try {
            DriverManager.registerDriver(new DynaDriver());
            LOGGER.log(Level.FINEST, "DynaDriver JDBC Driver loaded and registered to DriverManager");
        } catch (SQLException ex) {
            String errorMessage = "Could not register to DriverManager: " + DynaDriver.class.getName();
            LOGGER.log(Level.SEVERE, errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    private static final String JDBC_URL_PREFIX = "jdbc:dyna4jdbc:";

    private final ConnectionFactory connectionFactory;

    public DynaDriver() {
        this(ConnectionFactory.getInstance());
    }

    DynaDriver(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection connect(String url, Properties info) throws SQLException {

        if (!acceptsURL(url)) {
            /*
             * From the JavaDoc of java.sql.Driver.connect(String, Properties):
             * 
             * The driver should return "null" if it realizes it is the wrong
             * kind of driver to connect to the given URL.
             */
            return null;
        }

        /*
         * strip JDBC URL prefix from connection string and delegate the
         * remaining section to the connection factory
         */
        String factoryConfiguration = url.substring(JDBC_URL_PREFIX.length());

        return connectionFactory.newConnection(factoryConfiguration, info);
    }

    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith(JDBC_URL_PREFIX);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return DriverPropertyInfoFactory.getDriverPropertyInfo(url, info);
    }

    public int getMajorVersion() {
        return DriverInfo.DRIVER_VERSION_MAJOR;
    }

    public int getMinorVersion() {
        return DriverInfo.DRIVER_VERSION_MINOR;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return PARENT_LOGGER;
    }
}
