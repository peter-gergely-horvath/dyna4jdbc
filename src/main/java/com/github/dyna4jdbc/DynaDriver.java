
package com.github.dyna4jdbc;

import com.github.dyna4jdbc.internal.MisconfigurationSQLException;
import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtil;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;


public class DynaDriver implements java.sql.Driver {

    public static final int DRIVER_VERSION_MAJOR = 0;
    public static final int DRIVER_VERSION_MINOR = 1;

    public static final String DRIVER_SHORT_NAME = "Dyna4JDBC";
    public static final String DRIVER_NAME = String.format("%s Dynamic Languages For JDBC Driver", DRIVER_SHORT_NAME);
    public static final String DRIVER_VERSION = String.format("%s.%s", DRIVER_VERSION_MAJOR, DRIVER_VERSION_MINOR);


    private static final String JDBC_URL_PREFIX = "jdbc:dyna4jdbc:";


    private static final Logger LOGGER = Logger.getLogger(DynaDriver.class.getPackage().getName());


    static {
        try {
            DriverManager.registerDriver(new DynaDriver());
        }
        catch (SQLException ex) {
            throw new RuntimeException("Could not register to DriverManager: " + DynaDriver.class.getName(), ex);
        }
    }

    private final ConnectionFactory connectionFactory;

    public DynaDriver() {
        this(ConnectionFactory.getInstance());
    }

    DynaDriver(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection connect(String url, Properties info) throws SQLException {
        try {
            if (!acceptsURL(url)) SQLError.CONNECT_FAILED_INVALID_URL.raiseException(url);

            String[] bridgeNameAndConfig = url.replace(JDBC_URL_PREFIX, "").split(":", 2);

            String bridgeName = bridgeNameAndConfig[0];
            String config = bridgeNameAndConfig.length == 2 ? bridgeNameAndConfig[1] : null;

            return doConnect(bridgeName, config, info);
        }
        catch (MisconfigurationSQLException ex)
        {
        	String causeMessage = ExceptionUtil.getRootCauseMessage(ex);
            throw SQLError.INVALID_CONFIGURATION.raiseException(ex, causeMessage);
        }
        catch (Exception ex)
        {
        	String causeMessage = ExceptionUtil.getRootCauseMessage(ex);
            throw SQLError.CONNECT_FAILED_GENERIC.raiseException(ex, causeMessage);
        }
    }

    protected Connection doConnect(String name, String config, Properties info) throws Exception {
        return connectionFactory.newConnection(name, config, info);
    }


    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith(JDBC_URL_PREFIX);
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return DRIVER_VERSION_MAJOR;
    }

    public int getMinorVersion() {
        return DRIVER_VERSION_MINOR;
    }

    public boolean jdbcCompliant() {
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return LOGGER;
    }
}
