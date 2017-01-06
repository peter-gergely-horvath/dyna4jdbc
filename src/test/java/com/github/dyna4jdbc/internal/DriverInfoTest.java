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

 
package com.github.dyna4jdbc.internal;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.dyna4jdbc.DriverTest;
import com.github.dyna4jdbc.internal.DriverInfo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Peter G. Horvath
 */
public class DriverInfoTest {

    private static final String MAVEN_PROPERTIES_LOCATION = "/test.maven.properties";


    private final Properties mavenPropertiesCapturedDuringBuild = new Properties();

    @BeforeClass
    public void beforeClass() throws IOException {

        try(InputStream is =
                    DriverTest.class.getResourceAsStream(MAVEN_PROPERTIES_LOCATION)) {
            if(is == null) throw new IllegalStateException("Not found: " + MAVEN_PROPERTIES_LOCATION);

            mavenPropertiesCapturedDuringBuild.load(is);
        }
    }

    @Test
    public void testDriverReportsValidMajorVersionNumber() throws SQLException {

        String projectVersion = mavenPropertiesCapturedDuringBuild.getProperty("project.version");

        String majorVersionString = projectVersion.split("\\.")[0];

        assertEquals(Integer.parseInt(majorVersionString), DriverInfo.DRIVER_VERSION_MAJOR);
    }

    @Test
    public void testDriverReportsValidMinorVersionNumber() throws SQLException {

        String projectVersion = mavenPropertiesCapturedDuringBuild.getProperty("project.version");

        String minorVersionString = projectVersion.split("\\.")[1];

        // remove non-number characters, e.g. "-SNAPSHOT"
        minorVersionString = minorVersionString.replaceAll("\\D", "");

        assertEquals(Integer.parseInt(minorVersionString), DriverInfo.DRIVER_VERSION_MINOR);
    }

    @Test
    public void testDriverReportsValidProductName() throws SQLException {

        String projectName = mavenPropertiesCapturedDuringBuild.getProperty("project.name");

        assertEquals(projectName, DriverInfo.DRIVER_NAME);
    }
}
