package com.github.dyna4jdbc.internal.common.util.classpath;

import java.util.List;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

public interface ClassLoaderFactory {

    ClassLoader newClassLoaderFromClasspath(List<String> classpath) throws MisconfigurationException;

}
