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
	public void testEmptyValueHandling() {
		
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
