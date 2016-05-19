package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultTypeHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

public final class ProcessRunnerConnection extends AbstractConnection  {

	private final TypeHandlerFactory typeHandlerFactory;
	private final Configuration configuration;
	private final ProcessRunnerScriptExecutor scriptExecutor;


    public ProcessRunnerConnection(String parameters, Properties properties) throws SQLException, MisconfigurationException
    {
        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
		configuration = configurationFactory.newConfigurationFromParameters(parameters, properties);
        typeHandlerFactory = DefaultTypeHandlerFactory.getInstance(configuration);
        
        this.scriptExecutor = new ProcessRunnerScriptExecutor(configuration);
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        checkNotClosed();
        return new GenericDatabaseMetaData(this, System.getProperty("os.name"), System.getProperty("os.version") );
    }

    @Override
    protected AbstractStatement<?> createStatementInternal() throws SQLException {
        checkNotClosed();
        ScriptOutputHandlerFactory outputHandlerFactory = new DefaultScriptOutputHandlerFactory(typeHandlerFactory, configuration);
        
		
		return new ProcessRunnerStatement(this, outputHandlerFactory, scriptExecutor);
    }
    
    @Override
    protected void closeInternal() throws SQLException {
    	scriptExecutor.close();
    }

}
