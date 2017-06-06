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
import com.github.dyna4jdbc.internal.ScriptExecutor;
import com.github.dyna4jdbc.internal.common.jdbc.base.ScriptConnection;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

public class ScriptEngineConnection extends ScriptConnection {

    private final Configuration configuration;
    private final ColumnHandlerFactory columnHandlerFactory;
    private final ScriptEngineScriptExecutor scriptExecutor;

    public ScriptEngineConnection(String parameters, Properties properties)
            throws SQLException, MisconfigurationException {

        if (parameters == null || "".equals(parameters.trim())) {
            throw JDBCError.INVALID_CONFIGURATION.raiseSQLException(
                    "Scrip Engine Name not specified");
        }

        String[] engineNameAndParameters = parameters.split(":", 2);

        String engineName = engineNameAndParameters[0];
        String configurationString = ArrayUtils.tryGetByIndex(engineNameAndParameters, 1);

        if (engineName == null || "".equals(engineName.trim())) {
            throw JDBCError.INVALID_CONFIGURATION.raiseSQLException(
                    "Scrip Engine Name is not specified");
        }

        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        this.configuration =
                configurationFactory.newConfigurationFromParameters(configurationString, properties);


        this.columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(configuration);

        ScriptEngineScriptExecutorFactory scriptExecutorFactory =
                DefaultScriptEngineScriptExecutorFactory.getInstance(configuration);

        this.scriptExecutor = scriptExecutorFactory.newInterpreterEnhancedScriptEngineScriptExecutor(engineName);

        String initScriptPath = configuration.getInitScriptPath();
        if (initScriptPath != null) {
            executeInitScript(initScriptPath);
        }
    }

    @Override
    protected final DatabaseMetaData getMetaDataInternal() throws SQLException {

        String name = scriptExecutor.getHumanFriendlyName();
        String version = scriptExecutor.getVersion();

        return new GenericDatabaseMetaData(this, name, version);
    }



    @Override
    protected final ColumnHandlerFactory getColumnHandlerFactory() {
        return columnHandlerFactory;
    }

    @Override
    protected final Configuration getConfiguration() {
        return configuration;
    }

    @Override
    protected final ScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }
}

