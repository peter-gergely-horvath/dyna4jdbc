package com.github.dyna4jdbc.internal.config;

import java.util.Properties;

public interface ConfigurationFactory {

	Configuration newConfigurationFromParameters(String config, Properties props) throws MisconfigurationException;
}
