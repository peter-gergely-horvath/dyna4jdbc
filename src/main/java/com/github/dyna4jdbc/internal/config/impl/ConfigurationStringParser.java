package com.github.dyna4jdbc.internal.config.impl;

import java.util.Properties;
import java.util.StringTokenizer;

public class ConfigurationStringParser {
	
	private static final ConfigurationStringParser INSTANCE = new ConfigurationStringParser();

	public static ConfigurationStringParser getInstance() {
		return INSTANCE;
	}
	
	private ConfigurationStringParser() {
		
	}
	
	public Properties parseStringToProperties(String configuirationString) {
		Properties properties = new Properties();
		
		if(configuirationString != null) {
		     StringTokenizer st = new StringTokenizer(configuirationString, ";");
		     while (st.hasMoreTokens()) {
		         String keyValuePair = st.nextToken();
		         String[] keyAndValue = keyValuePair.split("=", 2);
		         
		         if(keyAndValue.length == 1) {
		        	 throw new IllegalArgumentException(
		        			 String.format("Configuration string '%s' cannot be interpreted as '=' "
		        			 		+ "character separated key-value pairs: "
		        			 		+ "this is invalid!", configuirationString));
		         }
		         
		         
		         String key = keyAndValue[0];
		         String value =  keyAndValue[1];
		         
		         if(properties.containsKey(key)) {
		        	 /* configuration string contains duplicated entries;
		        	  * e.g. "foo=x;foo=y"
		        	  * Fail-fast in such cases by throwing exception
		        	  */
		        	 throw new IllegalArgumentException(
		        			 String.format("Configuration string contains duplicated key '%s': "
		        			 		+ "this is invalid!", key));
		         }
		         
		         properties.setProperty(key, value);
		     }
		}
		return properties;
	}
}
