package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Peter Horvath
 */
public class DefaultConfigurationFactoryTest {

    private DefaultConfigurationFactory defaultConfigurationFactory;

    @BeforeTest
    public void beforeClass() {
        this.defaultConfigurationFactory = DefaultConfigurationFactory.getInstance();
    }

    @Test
    public void testNullProperties() throws MisconfigurationException {

        Configuration configuration =
                defaultConfigurationFactory.newConfigurationFromParameters("foo=bar", null);

        assertNotNull(configuration);
    }

    @Test
    public void testNullString() throws MisconfigurationException {

        Configuration configuration =
                defaultConfigurationFactory.newConfigurationFromParameters(null, new Properties());

        assertNotNull(configuration);
    }

    @Test
    public void testNullStringAndNullProperties() throws MisconfigurationException {

        Configuration configuration =
                defaultConfigurationFactory.newConfigurationFromParameters(null, null);

        assertNotNull(configuration);
    }

    @Test(expectedExceptions = MisconfigurationException.class)
    public void testPropertiesWithNonStringKey() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(new Object(), "foobar");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }


}
