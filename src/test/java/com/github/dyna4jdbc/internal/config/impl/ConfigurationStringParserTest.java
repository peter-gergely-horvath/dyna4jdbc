package com.github.dyna4jdbc.internal.config.impl;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.internal.config.impl.ConfigurationStringParser;

public class ConfigurationStringParserTest {
	
	private ConfigurationStringParser configurationStringParser;

	@BeforeTest
	public void beforeClass() {
		this.configurationStringParser = ConfigurationStringParser.getInstance();
	}
	
	
	@Test
	public void testNullArgumentYieldsEmptyProperties() {
		
		Properties properties = configurationStringParser.parseStringToProperties(null);
		
		assertNotNull(properties);
		
		assertTrue(properties.isEmpty(), "properties contains " + properties.size() + " entries");
	}
	
	@Test
	public void testStandardArgumentParsing() {
		Properties properties = configurationStringParser.parseStringToProperties("foo=fooValue;bar=barValue");
		
		assertEquals(properties.getProperty("foo"), "fooValue");
		
		assertEquals(properties.getProperty("bar"), "barValue");
		
		
		// test that there are no additional bogus entries in properties
		assertTrue(properties.keySet().size() == 2);
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testHandlingOfDuplicateEntriesInString() {
		
		configurationStringParser.parseStringToProperties("foo=fooValue1;bar=barValue;foo=fooValue2");
	}
	
	@Test
	public void testEmptryValueHandling() {
		
		Properties properties = configurationStringParser.parseStringToProperties("foo=fooValue;bar=;baz=bazValue");
		
		assertNotNull(properties);
		
		assertEquals(properties.getProperty("foo"), "fooValue");
		assertEquals(properties.getProperty("bar"), "");
		assertEquals(properties.getProperty("baz"), "bazValue");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class)
	public void testHandlingOfStringWithoutEqualsSign() {
		
		configurationStringParser.parseStringToProperties("foobar");
	}
}
