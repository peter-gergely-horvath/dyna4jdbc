package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.TypeHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultTypeHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;

public class DefaultScriptEngineConnection extends AbstractConnection implements OutputCapturingScriptExecutor {

    //CHECKSTYLE.OFF: VisibilityModifier
    protected final ScriptEngine engine;
    protected final IOHandlerFactory ioHandlerFactory;
    //CHECKSTYLE.ON: VisibilityModifier

    private final TypeHandlerFactory typeHandlerFactory;
    private final Configuration configuration;

    public DefaultScriptEngineConnection(String parameters, Properties properties)
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

        this.engine = loadEngineByName(engineName);

        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        this.configuration =
                configurationFactory.newConfigurationFromParameters(configurationString, properties);

        this.typeHandlerFactory = DefaultTypeHandlerFactory.getInstance(configuration);
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);

    }

    private static ScriptEngine loadEngineByName(String engineName) throws SQLException {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(engineName);
        if (scriptEngine == null) {
            throw JDBCError.LOADING_SCRIPTENGINE_FAILED.raiseSQLException(engineName);
        }

        return scriptEngine;
    }

    @Override
    public final DatabaseMetaData getMetaData() throws SQLException {
        checkNotClosed();

        ScriptEngineFactory factory = engine.getFactory();

        String engineDescription = null;
        String engineVersion = null;

        if (factory != null) {
            String engineName = factory.getEngineName();
            String languageVersion = factory.getLanguageVersion();

            engineDescription = String.format("%s (%s)", engineName, languageVersion);
            engineVersion = factory.getEngineVersion();
        }

        return new GenericDatabaseMetaData(this, engineDescription, engineVersion);
    }

    @Override
    protected final AbstractStatement<?> createStatementInternal() throws SQLException {
        checkNotClosed();
        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(typeHandlerFactory, configuration);

        return new OutputHandlingStatement<>(this, outputHandlerFactory, this);
    }


    //CHECKSTYLE.OFF: DesignForExtension
    @Override
    public void executeScriptUsingStreams(
            String script,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        synchronized (engine) {
            Writer originalWriter = engine.getContext().getWriter();
            Writer originalErrorWriter = engine.getContext().getErrorWriter();

            try {

                if (stdOutputStream != null) {

                    PrintWriter outputPrintWriter =
                            ioHandlerFactory.newPrintWriter(stdOutputStream, true);

                    engine.getContext().setWriter(outputPrintWriter);
                }

                if (errorOutputStream != null) {

                    PrintWriter errorPrintWriter =
                            ioHandlerFactory.newPrintWriter(errorOutputStream, true);

                    engine.getContext().setErrorWriter(errorPrintWriter);
                }

                engine.eval(script);
            } catch (ScriptException e) {
                throw new ScriptExecutionException(e);
            } finally {
                engine.getContext().setWriter(originalWriter);
                engine.getContext().setErrorWriter(originalErrorWriter);
            }
        }
    }
    //CHECKSTYLE.ON: DesignForExtension
}
