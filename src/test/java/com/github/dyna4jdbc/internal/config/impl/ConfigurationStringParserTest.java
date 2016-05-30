/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
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

import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.*;

public class ConfigurationStringParserTest {
	
	private ConfigurationStringParser configurationStringParser;

	@BeforeTest
	public void beforeClass() {
		this.configurationStringParser = ConfigurationStringParser.getInstance();
	}
	
	
	@Test
	public void testNullArgumentYieldsEmptyProperties() throws MisconfigurationException {
		
		Properties properties = configurationStringParser.parseStringToProperties(null);
		
		assertNotNull(properties);
		
		assertTrue(properties.isEmpty(), "properties contains " + properties.size() + " entries");
	}
	
	@Test
	public void testStandardArgumentParsing() throws MisconfigurationException {
		Properties properties = configurationStringParser.parseStringToProperties("foo=fooValue;bar=barValue");
		
		assertEquals(properties.getProperty("foo"), "fooValue");
		
		assertEquals(properties.getProperty("bar"), "barValue");
		
		
		// test that there are no additional bogus entries in properties
		assertTrue(properties.keySet().size() == 2);
	}
	
	@Test(expectedExceptions=MisconfigurationException.class)
	public void testHandlingOfDuplicateEntriesInString() throws MisconfigurationException {
		
		configurationStringParser.parseStringToProperties("foo=fooValue1;bar=barValue;foo=fooValue2");
	}
	
	@Test
	public void testEmptyValueHandling() throws MisconfigurationException {
		
		Properties properties = configurationStringParser.parseStringToProperties("foo=fooValue;bar=;baz=bazValue");
		
		assertNotNull(properties);
		
		assertEquals(properties.getProperty("foo"), "fooValue");
		assertEquals(properties.getProperty("bar"), "");
		assertEquals(properties.getProperty("baz"), "bazValue");
	}
	
	@Test(expectedExceptions=MisconfigurationException.class)
	public void testHandlingOfStringWithoutEqualsSign() throws MisconfigurationException {
		
		configurationStringParser.parseStringToProperties("foobar");
	}
}
