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

 
package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import com.github.dyna4jdbc.internal.ScriptExecutor;
import com.github.dyna4jdbc.internal.common.jdbc.base.ScriptConnection;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.ConfigurationEntry;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;


public class ProcessRunnerConnection extends ScriptConnection {

    private final Configuration configuration;
    private final ColumnHandlerFactory columnHandlerFactory;
    private final ExternalProcessScriptExecutor scriptExecutor;

    public ProcessRunnerConnection(
            String parameters,
            Properties properties)
            throws SQLException, MisconfigurationException {

        this(parameters, checkProperties(parameters, properties),
                DefaultExternalProcessScriptExecutorFactory.getInstance());
    }

    protected ProcessRunnerConnection(
            String parameters,
            Properties properties,
            ExternalProcessScriptExecutorFactory processRunnerFactory)
            throws SQLException, MisconfigurationException {
        
        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        this.configuration = configurationFactory.newConfigurationFromParameters(parameters, properties);

        this.columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(this.configuration);

        this.scriptExecutor = processRunnerFactory.newExternalProcessScriptExecutor(this.configuration);

        String initScriptPath = this.configuration.getInitScriptPath();
        if (initScriptPath != null) {
            executeInitScript(initScriptPath);
        }
    }

    private static Properties checkProperties(String config, Properties properties) throws MisconfigurationException {

        DefaultConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        Configuration configuration = configurationFactory.newConfigurationFromParameters(config, properties);

        if (configuration.getEndOfDataPattern() == null
                && configuration.getExternalCallQuietPeriodThresholdMs() == 0) {
            throw MisconfigurationException.forMessage(
                    "External process connection requires either '%s' or '%s' to be specified.",
                    ConfigurationEntry.ENF_OF_DATA_REGEX.getKey(),
                    ConfigurationEntry.EXTERNAL_COMMAND_NO_OUTPUT_EXPIRATION_INTERVAL_MS.getKey());
        }

        if (!configuration.getClasspath().isEmpty()) {
            throw MisconfigurationException.forMessage(
                    "External process connection cannot handle configuration '%s'",
                    ConfigurationEntry.CLASSPATH.getKey());
        }

        return properties;

    }

    @Override
    protected final void onClose() {
        scriptExecutor.close();
    }

    //CHECKSTYLE.OFF: DesignForExtension : incorrect detection of "is not designed for extension"
    @Override
    protected DatabaseMetaData getMetaDataInternal() throws SQLException {

        return new GenericDatabaseMetaData(this,
                System.getProperty("os.name"),
                System.getProperty("os.version"));
    }
    //CHECKSTYLE.ON: DesignForExtension : incorrect detection of "is not designed for extension"



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
