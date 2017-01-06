/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.config.impl;

import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.DuplicatedKeyInConfigurationException;
import com.github.dyna4jdbc.internal.config.InvalidConfigurationValueException;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.*;

import static com.github.dyna4jdbc.internal.common.util.collection.CollectionTestHelper.assertIteratorReturnsValues;
/**
 * @author Peter G. Horvath
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

    @Test(expectedExceptions = DuplicatedKeyInConfigurationException.class)
    public void testDuplicatedPropertiesBetweenConfigStringAndProperties() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put("foo", "ABC");

        defaultConfigurationFactory.newConfigurationFromParameters("foo=XYZ", properties);
    }

    @Test
    public void testValidConfiguration() throws MisconfigurationException {

        Properties properties = new Properties();

        String cellSeparator = "|";
        String skipFirstLine = "false";
        String charSet = "UTF-16";
        String preferMultipleResultSets = "true";

        properties.put(ConfigurationEntry.CELL_SEPARATOR.getKey(), cellSeparator);
        properties.put(ConfigurationEntry.SKIP_FIRST_RESULT_LINE.getKey(), skipFirstLine);
        properties.put(ConfigurationEntry.CHARSET.getKey(), charSet);
        properties.put(ConfigurationEntry.PREFER_MULTIPLE_RESULT_SETS.getKey(), preferMultipleResultSets);

        Configuration configuration = defaultConfigurationFactory.newConfigurationFromParameters(null, properties);

        assertEquals(String.valueOf(configuration.getCellSeparator()),
                cellSeparator);

        assertEquals(configuration.getSkipFirstLine(),
                Boolean.valueOf(skipFirstLine).booleanValue());

        assertEquals(configuration.getConversionCharset(),
                charSet);

        assertEquals(configuration.getPreferMultipleResultSets(),
                Boolean.valueOf(preferMultipleResultSets).booleanValue());

    }
    
    @Test
    public void testMultipleClasspathEntriesConfiguration() throws MisconfigurationException {

        StringBuilder configurationStringBuilder = new StringBuilder();
        
        String cellSeparator = "|";
        String skipFirstLine = "false";
        String charSet = "UTF-16";
        String preferMultipleResultSets = "true";
        
        configurationStringBuilder
            .append(ConfigurationEntry.CELL_SEPARATOR.getKey()).append("=").append(cellSeparator).append(";")
            .append(ConfigurationEntry.SKIP_FIRST_RESULT_LINE.getKey()).append("=").append(skipFirstLine).append(";")
            .append(ConfigurationEntry.CHARSET.getKey()).append("=").append(charSet).append(";")
            .append(ConfigurationEntry.PREFER_MULTIPLE_RESULT_SETS.getKey()).append("=").append(preferMultipleResultSets).append(";")
            .append(ConfigurationEntry.CLASSPATH.getKey()).append("=").append("foo.jar,").append("bar.jar,").append("baz.jar").append(";");

        Configuration configuration = defaultConfigurationFactory.newConfigurationFromParameters(
                configurationStringBuilder.toString(), new Properties());

        assertEquals(String.valueOf(configuration.getCellSeparator()),
                cellSeparator);

        assertEquals(configuration.getSkipFirstLine(),
                Boolean.valueOf(skipFirstLine).booleanValue());

        assertEquals(configuration.getConversionCharset(),
                charSet);

        assertEquals(configuration.getPreferMultipleResultSets(),
                Boolean.valueOf(preferMultipleResultSets).booleanValue());
        
        assertEquals(configuration.getPreferMultipleResultSets(),
                Boolean.valueOf(preferMultipleResultSets).booleanValue());

        assertIteratorReturnsValues(configuration.getClasspath().iterator(),
                "foo.jar", "bar.jar", "baz.jar");
                
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testInvalidCellSeparator() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.CELL_SEPARATOR.getKey(), "foobar");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testNullCellSeparator() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.CELL_SEPARATOR.getKey(), "");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testInvalidSkipFirstResultLine() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.SKIP_FIRST_RESULT_LINE.getKey(), "foobar");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testNullSkipFirstResultLine() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.SKIP_FIRST_RESULT_LINE.getKey(), "");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testInvalidPreferMultipleResultSets() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.PREFER_MULTIPLE_RESULT_SETS.getKey(), "foobar");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testNullPreferMultipleResultSets() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.PREFER_MULTIPLE_RESULT_SETS.getKey(), "");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testInvalidCharSet() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.CHARSET.getKey(), "foobar");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }

    @Test(expectedExceptions = InvalidConfigurationValueException.class)
    public void testNullCharSet() throws MisconfigurationException {

        Properties properties = new Properties();

        properties.put(ConfigurationEntry.CHARSET.getKey(), "");

        defaultConfigurationFactory.newConfigurationFromParameters(null, properties);
    }
}
