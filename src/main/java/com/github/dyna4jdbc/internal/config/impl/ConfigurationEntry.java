package com.github.dyna4jdbc.internal.config.impl;

import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

import com.github.dyna4jdbc.internal.MisconfigurationSQLException;

enum ConfigurationEntry {
	
	CELL_SEPARATOR("cellSeparator", "\t", "The character used as separator character.") {
		@Override
		void setConfiguration(ConfigurationImpl config, String value) throws SQLException {
			if(value != null && value.length() > 1) {
				throw MisconfigurationSQLException.forMessage(
						"A singe character is expected for %s, but was: '%s'", this.key, value);
			}
			
			char charAtZero = value.charAt(0);

			config.setCellSeparator(charAtZero);
		}
	};
	
	
	
	final String key;
	final String defaultValue;
	final String description;

	ConfigurationEntry(String key, String defaultValue, String description) {
		this.key = key;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	abstract void setConfiguration(ConfigurationImpl config, String value) throws SQLException;
	
	public DriverPropertyInfo getDriverPropertyInfo() {
		DriverPropertyInfo driverPropertyInfo = new DriverPropertyInfo(this.key, defaultValue);
		driverPropertyInfo.description = this.description;
		return driverPropertyInfo;
	}
}
