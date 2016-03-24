package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.SQLError;

enum ConfigurationEntry {
	
	CELL_SEPARATOR("cellSeparator", "\t") {
		@Override
		void setConfiguration(ConfigurationImpl config, String value) throws Exception {
			if(value != null && value.length() > 1) {
				throw SQLError.raiseInternalIllegalStateRuntimeException(
						"%s should contain a singe character, but was: [%s]", this.key, value);
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
	
	abstract void setConfiguration(ConfigurationImpl config, String value) throws Exception;

}
