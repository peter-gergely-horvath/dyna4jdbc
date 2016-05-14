package com.github.dyna4jdbc;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.sqlstate.SQLState;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.testng.Assert.*;


/**
 * @author Peter Horvath
 */
public class DriverTest {

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
    public void testDriverLoadAndConnectionAcquisition() throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:dyna4jdbc:scriptengine:JavaScript");) {

            assertNotNull(connection);
        }
    }

    @Test
    public void testDriverClass() throws SQLException {

        Driver driver = DriverManager.getDriver("jdbc:dyna4jdbc:scriptengine:JavaScript");

        assertTrue(driver instanceof DynaDriver);
    }

    @Test
    public void testDriverAcceptsCorrectUrl() throws SQLException {
        DynaDriver driver = new DynaDriver();

        assertTrue(driver.acceptsURL("jdbc:dyna4jdbc:scriptengine:JavaScript"));
    }

    @Test
    public void testDriverDoesNotAcceptsInvalidUrl() throws SQLException {
        DynaDriver driver = new DynaDriver();

        assertFalse(driver.acceptsURL("jdbc:foo:bar"));
    }

    @Test
    public void testDriverDoesNotThrowNPEFromAcceptsMethod() throws SQLException {
        DynaDriver driver = new DynaDriver();

        assertFalse(driver.acceptsURL(null));
    }

    @Test
    public void testDriverOnlyAcceptsTerminatedUrl() throws SQLException {
        DynaDriver driver = new DynaDriver();

        assertTrue(driver.acceptsURL("jdbc:dyna4jdbc:"));
        assertFalse(driver.acceptsURL("jdbc:dyna4jdbc"));
    }

    @Test
    public void testDriverReportsAsNotJdbcCompliant() throws SQLException {
        DynaDriver driver = new DynaDriver();

        assertFalse(driver.jdbcCompliant());
    }

    @Test
    public void testDriverReportsValidMajorVersionNumber() throws SQLException {
        DynaDriver driver = new DynaDriver();

        String projectVersion = mavenPropertiesCapturedDuringBuild.getProperty("project.version");

        String majorVersionString = projectVersion.split("\\.")[0];

        int majorVersion = driver.getMajorVersion();

        assertEquals(majorVersionString, Integer.toString(majorVersion));
    }

    @Test
    public void testDriverReportsValidMinorVersionNumber() throws SQLException {
        DynaDriver driver = new DynaDriver();

        String projectVersion = mavenPropertiesCapturedDuringBuild.getProperty("project.version");

        String minorVersionString = projectVersion.split("\\.")[1];

        // remove non-number characters, e.g. "-SNAPSHOT"
        minorVersionString = minorVersionString.replaceAll("\\D", "");

        int minorVersion = driver.getMinorVersion();

        assertEquals(Integer.toString(minorVersion), minorVersionString);
    }

    @Test
    public void testDriverReturnsParentLogger() throws SQLException {
        DynaDriver driver = new DynaDriver();

        assertNotNull(driver.getParentLogger());
    }

    @Test
    public void testDriverPropertyInforIsNotEmpty() throws SQLException {
        DynaDriver driver = new DynaDriver();

        DriverPropertyInfo[] propertyInfo =
                driver.getPropertyInfo("jdbc:dyna4jdbc:scriptengine:JavaScript", new Properties());

        assertNotNull( propertyInfo );
        assertTrue( propertyInfo.length > 0 );
    }

    @Test
    public void testDriverDelegatesConnectionToConnectionFactory() throws SQLException {

        ConnectionFactory mockConnectionFactory = new ConnectionFactory() {
            @Override
            Connection newConnection(String factoryConfiguration, Properties info) throws SQLException {

                assertEquals(factoryConfiguration, "foo:bar/baz");

                return null;
            }
        };

        DynaDriver driver = new DynaDriver(mockConnectionFactory);

        driver.connect("jdbc:dyna4jdbc:foo:bar/baz", new Properties());
    }

    @Test
    public void testHandlingOfInvalidScriptEngine() throws SQLException {

        DynaDriver driver = new DynaDriver();

        try {

            driver.connect("jdbc:dyna4jdbc:scriptengine:FooBar", new Properties());

            fail("Should throw an exception");
        } catch (SQLException sqlex) {

            String message = sqlex.getMessage();

            assertTrue(message.contains(JDBCError.CONNECT_FAILED_EXCEPTION.name()),
                    String.format("Invalid message: '%s'", message) );

            String sqlState = sqlex.getSQLState();
            assertEquals(sqlState, SQLState.ERROR_CONNECTION_UNABLE_TO_ESTABILISH.code);
        }
    }
}
