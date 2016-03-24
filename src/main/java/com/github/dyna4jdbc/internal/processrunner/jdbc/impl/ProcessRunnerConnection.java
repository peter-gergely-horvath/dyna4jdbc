package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultTypeHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

public class ProcessRunnerConnection extends AbstractConnection implements OutputCapturingScriptExecutor {

    private final TypeHandlerFactory typeHandlerFactory;
	private final Configuration configuration;

    public ProcessRunnerConnection(String parameters, Properties properties) throws SQLException
    {
        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
		configuration = configurationFactory.newConfigurationFromParameters(parameters, properties);
        typeHandlerFactory = new DefaultTypeHandlerFactory();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        checkNotClosed();
        return new ProcessRunnerDatabaseMetaData(this);
    }

    public Statement createStatement() throws SQLException {
        checkNotClosed();
        ScriptOutputHandlerFactory outputHandlerFactory = new DefaultScriptOutputHandlerFactory(typeHandlerFactory, configuration);
        
		return new OutputHandlingStatement<ProcessRunnerConnection>(this, outputHandlerFactory, this);
    }


    public String getProductName() {
    	return System.getProperty("os.name");
    }

    public String getProductVersion() {
    	return System.getProperty("os.version");
    	
    }

	@Override
	public void executeScriptUsingCustomWriters(String script, Writer outWriter, Writer errorWriter)
			throws ScriptExecutionException {
        
		try {
		    
			PrintWriter outputPrintWriter = new PrintWriter(new BufferedWriter(outWriter));
			
			Process process = Runtime.getRuntime().exec(script);
		 
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	outputPrintWriter.println(line);
		    }
		 
		    reader.close();
		    outputPrintWriter.close();
		 
		} catch (IOException e) {
		    throw new ScriptExecutionException(e);
		}
	}
}