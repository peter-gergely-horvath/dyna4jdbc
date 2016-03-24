
package com.github.dyna4jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.config.impl.DriverPropertyInfoFactory;


public class DynaDriver implements java.sql.Driver {

	public static final int DRIVER_VERSION_MAJOR = 0;
	public static final int DRIVER_VERSION_MINOR = 5;

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

		if (!acceptsURL(url)) {
			SQLError.CONNECT_FAILED_INVALID_URL.raiseException(url);
		}

		String factoryConfiguration = url.replace(JDBC_URL_PREFIX, "");
		
		return connectionFactory.newConnection(factoryConfiguration, info);
	}

	public boolean acceptsURL(String url) throws SQLException {
		return url != null && url.startsWith(JDBC_URL_PREFIX);
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return DriverPropertyInfoFactory.getDriverPropertyInfo();
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
