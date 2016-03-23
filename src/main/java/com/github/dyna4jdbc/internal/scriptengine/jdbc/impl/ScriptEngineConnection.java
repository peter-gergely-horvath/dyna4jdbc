package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.io.Writer;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultTypeHandlerFactory;

public class ScriptEngineConnection extends AbstractConnection implements OutputCapturingScriptExecutor {

    final String engineName;

    private final String configuration;
    private final Properties properties;

    private final ScriptEngine engine;
    private final TypeHandlerFactory typeHandlerFactory;

    public ScriptEngineConnection(String parameters, Properties properties)
    {
        this.properties = properties;

        if(parameters == null || "".equals(parameters.trim())) {
            throw new IllegalArgumentException("Scrip Engine Name not specified");
        }

        String[] engineNameAndParameters = parameters.split(":", 2);

        engineName = engineNameAndParameters[0];
        configuration = engineNameAndParameters.length == 2 ? engineNameAndParameters[1] : null;

        if(engineName == null || "".equals(engineName.trim())) {
            throw new IllegalArgumentException("Scrip Engine Name not specified");
        }

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        this.engine = scriptEngineManager.getEngineByName(engineName);
        if(this.engine == null) {
            throw new IllegalArgumentException("ScriptEngine not found: " + engineName);
        }

        typeHandlerFactory = new DefaultTypeHandlerFactory();

    }

    public DatabaseMetaData getMetaData() throws SQLException {
        checkNotClosed();
        return new ScriptEngineDatabaseMetaData(this);
    }

    public Statement createStatement() throws SQLException {
        checkNotClosed();
        ScriptOutputHandlerFactory outputHandlerFactory = new DefaultScriptOutputHandlerFactory(typeHandlerFactory);
        
		return new OutputHandlingStatement(this, outputHandlerFactory, this);
    }


    public String getEngineDescription() {
        ScriptEngineFactory factory = engine.getFactory();
        if(factory != null) {
            String engineName = factory.getEngineName();
            String languageVersion = factory.getLanguageVersion();

            return String.format("%s (%s)", engineName, languageVersion);
        }

        return null;
    }

    public String getEngineVersion() {

        ScriptEngineFactory factory = engine.getFactory();
        if(factory != null) {
            return factory.getEngineVersion();
        }

        return null;
    }

	@Override
	public void executeScriptUsingCustomWriters(String script, Writer outWriter, Writer errorWriter)
			throws ScriptExecutionException {
        
        synchronized (engine) {
            Writer originalWriter = engine.getContext().getWriter();
            Writer originalErrorWriter = engine.getContext().getErrorWriter();

            try {

        		if (outWriter != null) {
                    engine.getContext().setWriter(outWriter);
                }
        		
                if (errorWriter != null) {
                    engine.getContext().setErrorWriter(errorWriter);
                }
            	
                engine.eval(script);
            } catch (ScriptException e) {
            	throw new ScriptExecutionException(e);
			}
            finally {
                engine.getContext().setWriter(originalWriter);
                engine.getContext().setErrorWriter(originalErrorWriter);
            }
        }
	}
}
