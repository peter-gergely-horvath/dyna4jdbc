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

 
package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingPreparedStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

public class ProcessRunnerConnection extends AbstractConnection {

    private final ColumnHandlerFactory columnHandlerFactory;
    private final Configuration configuration;
    private final ExternalProcessScriptExecutor scriptExecutor;

    public ProcessRunnerConnection(
            String parameters,
            Properties properties)
            throws SQLException, MisconfigurationException {

        this(parameters, properties, DefaultExternalProcessScriptExecutorFactory.getInstance());

        registerAsChild(() -> onClose());
    }
    
    protected ProcessRunnerConnection(
            String parameters,
            Properties properties,
            ExternalProcessScriptExecutorFactory processRunnerFactory)
            throws SQLException, MisconfigurationException {
        
        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        configuration = configurationFactory.newConfigurationFromParameters(parameters, properties);

        columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(configuration);
        
        this.scriptExecutor = processRunnerFactory.newExternalProcessScriptExecutor(configuration);

        registerAsChild(() -> onClose());
    }

    private void onClose() {
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
    protected final Statement createStatementInternal() throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);

        return new OutputHandlingStatement<>(this, outputHandlerFactory, scriptExecutor);
    }

    @Override
    protected final PreparedStatement prepareStatementInternal(String script) throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);

        return new OutputHandlingPreparedStatement<>(script, this, outputHandlerFactory, scriptExecutor);
    }
}
