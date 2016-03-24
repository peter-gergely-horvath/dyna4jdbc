package com.github.dyna4jdbc.internal.config.impl;

import java.sql.SQLException;

import com.github.dyna4jdbc.internal.MisconfigurationSQLException;

enum ConfigurationEntry {
	
	CELL_SEPARATOR("cellSeparator", "\t") {
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
	
	public final String key;
	public final String defaultValue;

	ConfigurationEntry(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}
	
	abstract void setConfiguration(ConfigurationImpl config, String value) throws SQLException;

}
