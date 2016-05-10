package com.github.dyna4jdbc.internal.common.util.config;

import java.util.Properties;
import java.util.StringTokenizer;

public class ConfigurationUtil {
	
	public static Properties readStringToProperties(String config) {
		Properties properties = new Properties();
		
		if(config != null) {
		     StringTokenizer st = new StringTokenizer(config, ";");
		     while (st.hasMoreTokens()) {
		         String keyValuePair = st.nextToken();
		         String[] keyAndValue = keyValuePair.split("=", 2);
		         String key = keyAndValue[0];

				 if(keyAndValue.length == 2) {
					 String value =  keyAndValue[1];

					 properties.setProperty(key, value);
				 }
		     }
		}
		return properties;
	}
}
