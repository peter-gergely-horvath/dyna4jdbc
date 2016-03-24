package com.github.dyna4jdbc.internal.config.impl;

import java.sql.DriverPropertyInfo;

public class DriverPropertyInfoFactory {

	private DriverPropertyInfoFactory() {
		// no instances allowed
	}
	
	public static DriverPropertyInfo[] getDriverPropertyInfo() {
		ConfigurationEntry[] input = ConfigurationEntry.values();
		DriverPropertyInfo[] result = new DriverPropertyInfo[input.length];
		for (int i = 0; i < input.length; i++) {
			result[i] = input[i].getDriverPropertyInfo();
		}
		return result;
	}
	
}
