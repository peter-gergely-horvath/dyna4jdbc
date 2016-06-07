package com.github.dyna4jdbc.internal.config.impl;

import java.util.Properties;

import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.DuplicatedKeyInConfigurationException;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

public final class DefaultConfigurationFactory implements ConfigurationFactory {

    private static final DefaultConfigurationFactory INSTANCE = new DefaultConfigurationFactory();

    public static DefaultConfigurationFactory getInstance() {
        return INSTANCE;
    }


    @Override
    public Configuration newConfigurationFromParameters(String config, Properties props)
            throws MisconfigurationException {

        ConfigurationImpl configuration = new ConfigurationImpl();

        Properties internalProps = ConfigurationStringParser.getInstance().parseStringToProperties(config);

        if (props != null) {
            for (Object propKey : props.keySet()) {
                if (!(propKey instanceof java.lang.String)) {
                    throw MisconfigurationException.forMessage("properties should only contain String keys!");
                } else {
                    String key = (String) propKey;
                    String propertyValue = props.getProperty(key);

                    if (internalProps.containsKey(key)) {
                        String previouslyFoundPropertyValue = internalProps.getProperty(key);

                        throw DuplicatedKeyInConfigurationException.forMessage(
                                "Duplicated configuration key encountered: %s=%s and %s=%s",
                                    key, propertyValue, key, previouslyFoundPropertyValue);
                    }


                    internalProps.setProperty(key, propertyValue);
                }
            }
        }

        for (ConfigurationEntry ce : ConfigurationEntry.values()) {
            String property = internalProps.getProperty(ce.getKey(), ce.getDefaultValue());
            ce.setConfiguration(configuration, property);
        }

        return configuration;
    }


}
