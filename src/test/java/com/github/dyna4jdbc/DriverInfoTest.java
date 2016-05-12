package com.github.dyna4jdbc;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * @author Peter Horvath
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
