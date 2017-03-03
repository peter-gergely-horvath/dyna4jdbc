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

 
package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.classpath.ClassLoaderFactory;
import com.github.dyna4jdbc.internal.common.util.classpath.DefaultClassLoaderFactory;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

final class BasicScriptEngineScriptExecutorFactory implements ScriptEngineScriptExecutorFactory {

    private static final Logger LOGGER = Logger.getLogger(BasicScriptEngineScriptExecutorFactory.class.getName());

    private final Configuration configuration;

    private BasicScriptEngineScriptExecutorFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    static BasicScriptEngineScriptExecutorFactory getInstance(Configuration configuration) {
        return new BasicScriptEngineScriptExecutorFactory(configuration);
    }

    @Override
    public ScriptEngineScriptExecutor newScriptEngineScriptExecutor(String scriptEngineName)
            throws SQLException, MisconfigurationException {

        ClassLoaderFactory classloaderFactory = DefaultClassLoaderFactory.getInstance();

        ScriptEngine engine = loadEngineByName(scriptEngineName, this.configuration, classloaderFactory);

        return new BasicScriptEngineScriptExecutor(scriptEngineName, engine, configuration);
    }

    private static ScriptEngine loadEngineByName(
            String engineName,
            Configuration configuration,
            ClassLoaderFactory classloaderFactory) throws SQLException, MisconfigurationException {

        List<String> classpath = configuration.getClasspath();

        ClassLoader classLoader;
        if (!classpath.isEmpty()) {
            LOGGER.fine("classpath specified, creating a ClassLoader for handling ...");
            classLoader = classloaderFactory.newClassLoaderFromClasspath(classpath);
        } else {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager(classLoader);
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(engineName);
        if (scriptEngine == null) {
            throw JDBCError.LOADING_SCRIPTENGINE_FAILED.raiseSQLException(engineName);
        }

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine(String.format("ScriptEngine '%s' loaded using class loader '%s'",
                    engineName, classLoader));
        }

        return scriptEngine;
    }
}
