
package com.github.dyna4jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.config.impl.DriverPropertyInfoFactory;


public class DynaDriver implements java.sql.Driver {

	private static final Logger LOGGER = Logger.getLogger(DynaDriver.class.getName());
	private static final Logger PARENT_LOGGER = Logger.getLogger(DynaDriver.class.getPackage().getName());

	public static final int DRIVER_VERSION_MAJOR;
	public static final int DRIVER_VERSION_MINOR;

	public static final String DRIVER_NAME;
	
	static {
		DriverInfo driverInfo = DriverInfo.getInstance();
		
		DRIVER_NAME = driverInfo.getProductName();
		DRIVER_VERSION_MAJOR = driverInfo.getMajorVersion();
		DRIVER_VERSION_MINOR = driverInfo.getMinorVersion();

		try {
			DriverManager.registerDriver(new DynaDriver());
		}
		catch (SQLException ex) {
			LOGGER.log(Level.SEVERE, "Could not register to DriverManager: " + DynaDriver.class.getName(), ex);
			throw new RuntimeException("Could not register to DriverManager: " + DynaDriver.class.getName(), ex);
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
			JDBCError.CONNECT_FAILED_INVALID_URL.raiseSQLException(url);
		}

		
		/* strip JDBC URL prefix from connection string and 
		 * delegate the remaining section to the connection factory */
		String factoryConfiguration = url.substring(JDBC_URL_PREFIX.length());

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
		return PARENT_LOGGER;
	}
}
