package com.github.dyna4jdbc.internal.config.impl;

import java.util.Properties;

import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

public final class DefaultConfigurationFactory implements ConfigurationFactory {

	private static final DefaultConfigurationFactory INSTANCE = new DefaultConfigurationFactory();
	
	public static DefaultConfigurationFactory getInstance() {
		return INSTANCE;
	}
	
	
	@Override
	public Configuration newConfigurationFromParameters(String config, Properties props) throws MisconfigurationException {
		ConfigurationImpl configuration = new ConfigurationImpl();
		
		
		Properties internalPropops = ConfigurationStringParser.getInstance().parseStringToProperties(config);
		
		for(Object propKey : props.keySet()) {
			if(!(propKey instanceof java.lang.String)) {
				throw MisconfigurationException.forMessage("properties should only contain String keys!");
			} else {
				String key = (String)propKey;
				
				if(internalPropops.containsKey(key)) {
					throw MisconfigurationException.forMessage(
							"duplicated configuration between JDBC URL and properties: %s", key);
				}
				
				internalPropops.setProperty(key, props.getProperty(key));
			}
		}
		
		for(ConfigurationEntry ce : ConfigurationEntry.values()) {
			String property = internalPropops.getProperty(ce.getKey(), ce.getDefaultValue());
			ce.setConfiguration(configuration, property);
		}
		
		return configuration;
	}



	
	
}
