package com.github.dyna4jdbc.internal;

import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public final class DriverInfo {

    public static final int DRIVER_VERSION_MAJOR;
    public static final int DRIVER_VERSION_MINOR;

    public static final String DRIVER_NAME;

    private static final String VERSION_PROPERTIES = "version.properties";

    private static final String PRODUCT_NAME = "product-name";
    private static final String VERSION = "version";

    private static final String VERSION_SEPARATOR = "\\.";

    private static final int ZERO_VERSION_NUMBER = 0;

    private static final int VERSION_INDEX_MAJOR = 0;
    private static final int VERSION_INDEX_MINOR = 1;

    private static final Logger LOGGER = Logger.getLogger(DriverInfo.class.getName());

    static {
        String productNameString;

        String majorVersionString = null;
        String minorVersionString = null;

        Properties props = null;

        productNameString = tryGetProductNameStringFromManifest();
        if (productNameString == null) {
            props = tryLoadPropertiesFromClasspath();
            productNameString = tryGetValueFromProperties(props, PRODUCT_NAME);
        }

        String versionString = tryGetVersionStringFromManifest();
        if (versionString == null) {
            if (props == null) {
                props = tryLoadPropertiesFromClasspath();
            }

            versionString = tryGetValueFromProperties(props, VERSION);
        }

        if (versionString != null) {

            String[] splitVersionString = versionString.split(VERSION_SEPARATOR);

            majorVersionString = splitVersionString[VERSION_INDEX_MAJOR];
            majorVersionString = removeNonNumberCharactersFromString(majorVersionString);

            if (splitVersionString.length >= 2) {
                minorVersionString = splitVersionString[VERSION_INDEX_MINOR];
                minorVersionString = removeNonNumberCharactersFromString(minorVersionString);
            }
        }

        DRIVER_NAME = productNameString;

        DRIVER_VERSION_MAJOR = safeParseToInteger(majorVersionString, ZERO_VERSION_NUMBER);
        DRIVER_VERSION_MINOR = safeParseToInteger(minorVersionString, ZERO_VERSION_NUMBER);
    }

    private DriverInfo() {
        // static utility class -- no instances allowed
    }

    private static int safeParseToInteger(String str, int defaultValueIfNull) {
        try {
            if (str == null) {
                return defaultValueIfNull;
            }

            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private static String removeNonNumberCharactersFromString(String str) {
        return str.replaceAll("\\D", "");
    }

    private static String tryGetProductNameStringFromManifest() {
        String productName = null;

        Package aPackage = DriverInfo.class.getPackage();
        if (aPackage != null) {
            productName = aPackage.getImplementationTitle();
            if (productName == null) {
                productName = aPackage.getSpecificationTitle();
            }
        }

        return productName;
    }

    private static String tryGetVersionStringFromManifest() {
        String version = null;

        Package aPackage = DriverInfo.class.getPackage();
        if (aPackage != null) {
            version = aPackage.getImplementationVersion();
            if (version == null) {
                version = aPackage.getSpecificationVersion();
            }
        }

        return version;
    }

    private static Properties tryLoadPropertiesFromClasspath() {

        Properties properties = null;

        try (InputStream is = DriverInfo.class.getResourceAsStream(VERSION_PROPERTIES)) {

            if (is != null) {
                properties = new Properties();
                properties.load(is);
            }

        } catch (Throwable t) {
            LOGGER.warning("Could not load properties file: " + t.getMessage());
        }

        return properties;
    }

    private static String tryGetValueFromProperties(Properties props, String key) {

        String returnValue = null;

        if (props != null) {
            returnValue = props.getProperty(key, null);
        }

        return returnValue;
    }

}
