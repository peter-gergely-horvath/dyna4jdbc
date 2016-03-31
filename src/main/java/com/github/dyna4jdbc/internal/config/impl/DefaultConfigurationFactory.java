package com.github.dyna4jdbc.internal.config.impl;

import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;

public class DefaultConfigurationFactory implements ConfigurationFactory {

	private static final DefaultConfigurationFactory INSTANCE = new DefaultConfigurationFactory();
	
	public static DefaultConfigurationFactory getInstance() {
		return INSTANCE;
	}
	
	
	@Override
	public Configuration newConfigurationFromParameters(String config, Properties props) throws SQLException {
		ConfigurationImpl configuration = new ConfigurationImpl();
		
		
		Properties internalPropops = new Properties();
		
		if(config != null) {
		     StringTokenizer st = new StringTokenizer(config, ";");
		     while (st.hasMoreTokens()) {
		         String keyValuePair = st.nextToken();
		         String[] keyAndValue = keyValuePair.split("=", 2);
		         String key = keyAndValue[0];
		         String value = keyAndValue.length == 2 ? keyAndValue[1] : null;
		         
		         internalPropops.setProperty(key, value);
		     }
		}
		
		for(Object propKey : props.keySet()) {
			if(!(propKey instanceof java.lang.String)) {
				throw SQLError.INVALID_CONFIGURATION.raiseSQLException(
						"properties should only contain String keys!");
			} else {
				String key = (String)propKey;
				
				if(internalPropops.containsKey(key)) {
					throw SQLError.INVALID_CONFIGURATION.raiseSQLException(
							"duplicated configuration between JDBC URL and properties: %s", key);
				}
				
				internalPropops.setProperty(key, props.getProperty(key));
			}
		}
		
		for(ConfigurationEntry ce : ConfigurationEntry.values()) {
			String property = internalPropops.getProperty(ce.key, ce.defaultValue);
			ce.setConfiguration(configuration, property);
		}
		
		return configuration;
	}
	
	
}
