package com.github.dyna4jdbc.internal.common.jdbc.generic;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractDatabaseMetaData;

public class GenericDatabaseMetaData extends AbstractDatabaseMetaData<Connection> {

	private String databaseProductName;
	private String databaseProductVersion;

	public GenericDatabaseMetaData(Connection connection, String databaseProductName, String databaseProductVersion) {
		super(connection);
		this.databaseProductName = databaseProductName;
		this.databaseProductVersion = databaseProductVersion;
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return databaseProductName;
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return databaseProductVersion;
	}
}
