package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

public final class ProcessRunnerConnection extends AbstractConnection {

    private final ColumnHandlerFactory columnHandlerFactory;
    private final Configuration configuration;
    private final ProcessRunnerScriptExecutor scriptExecutor;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public ProcessRunnerConnection(
            String parameters,
            Properties properties)
            throws SQLException, MisconfigurationException {

        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        configuration = configurationFactory.newConfigurationFromParameters(parameters, properties);

        columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(configuration);

        this.scriptExecutor = new ProcessRunnerScriptExecutor(configuration, executorService);

        registerAsChild(() -> onClose());
    }

    private void onClose() {
        try {
            scriptExecutor.close();
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkNotClosed();
        return new GenericDatabaseMetaData(this,
                System.getProperty("os.name"),
                System.getProperty("os.version"));
    }

    @Override
    protected Statement createStatementInternal() throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);

        return new OutputHandlingStatement<>(this, outputHandlerFactory, scriptExecutor);
    }
    
    @Override
    protected PreparedStatement prepareStatementInternal(String script) throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);
        
        return new OutputHandlingPreparedStatement<>(script, this, outputHandlerFactory, scriptExecutor);
    }
}
