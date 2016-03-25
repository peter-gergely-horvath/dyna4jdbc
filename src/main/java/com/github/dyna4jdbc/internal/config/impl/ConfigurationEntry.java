package com.github.dyna4jdbc.internal.config.impl;

import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Arrays;

import com.github.dyna4jdbc.internal.MisconfigurationSQLException;

enum ConfigurationEntry {

	CELL_SEPARATOR("cellSeparator", "\t", "The character used as separator character. Default is TAB (\\t).") {
		@Override
		void setConfiguration(ConfigurationImpl config, String value) throws SQLException {
			if (value == null || (value != null && value.length() != 1)) {
				throw MisconfigurationSQLException.forMessage(
						"A singe character is expected for %s, but was: '%s'",
						this.key, value);
			}

			char charAtZero = value.charAt(0);

			config.setCellSeparator(charAtZero);
		}
	},
	SKIP_FIRST_RESULT_LINE("skipFirstLine", "false",
			"If set to true, the first output line is omitted from the result. Default is false.") {
		@Override
		void setConfiguration(ConfigurationImpl config, String value) throws SQLException {

			if (value == null || "".equals(value) || "".equals(value.trim())) {
				throw MisconfigurationSQLException
						.forMessage("Value for %s cannot be null/whitespace only, but was '%s'", 
								this.key, value);
			}

			switch (value) {
			case "true":
				config.setSkipFirstLine(true);
				break;

			case "false":
				config.setSkipFirstLine(false);
				break;

			default:
				throw MisconfigurationSQLException
						.forMessage("Value for %s must either be 'true' or 'false', but was '%s'", 
								this.key, value);
			}
		}
		
		@Override
		public DriverPropertyInfo getDriverPropertyInfo() {
			DriverPropertyInfo propertyInfo = super.getDriverPropertyInfo();
			propertyInfo.choices = new String[] {"true", "false"};
			return propertyInfo;
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
