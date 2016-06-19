package com.github.dyna4jdbc.internal.common.util.classpath;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

public final class DefaultClassLoaderFactory implements ClassLoaderFactory {

    private static final Logger LOGGER = Logger.getLogger(DefaultClassLoaderFactory.class.getName());
    
    private static final DefaultClassLoaderFactory INSTANCE = new DefaultClassLoaderFactory();

    private DefaultClassLoaderFactory() {
        // private constructor to prevent external instantiation
    }

    public static DefaultClassLoaderFactory getInstance() {
        return INSTANCE;
    }
    
    private static final class CreateURLClassLoaderPrivilegedAction implements PrivilegedAction<ClassLoader> {
        private URL[] urls;

        private CreateURLClassLoaderPrivilegedAction(URL[] urlArray) {
            this.urls = urlArray;
        }

        public ClassLoader run() {
            LOGGER.finer("PrivilegedAction.run() dispatched successfully");
            
            return new URLClassLoader(urls);
        }
    }

    @Override
    public ClassLoader newClassLoaderFromClasspath(List<String> classpath) throws MisconfigurationException {

        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(String.format(
                    "Preparing to construct class loader from %s URLs", 
                    classpath.size()));
        }

        ArrayList<URL> urlList = new ArrayList<>(classpath.size());

        for (String classpathEntry : classpath) {

            try {
                URL url = new File(classpathEntry).toURI().toURL();

                urlList.add(url);

                if (LOGGER.isLoggable(Level.FINER)) {
                    LOGGER.finer("\tAdded classpath URL: " + classpathEntry);
                }

            } catch (MalformedURLException e) {
                throw MisconfigurationException.forMessage("Failed to convert classpath entry to URL: '%s'",
                        classpathEntry);
            }
        }
        URL[] urlArray = urlList.toArray(new URL[0]);

        LOGGER.finer("Invoking PrivilegedAction to create the class loader ...");

        ClassLoader classLoader = AccessController.doPrivileged(
                new CreateURLClassLoaderPrivilegedAction(urlArray));

        LOGGER.finer("PrivilegedAction was successfully invoked: ClassLoader instantiated!");

        return classLoader;
    }

}
