package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.*;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingPreparedStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.classpath.ClassLoaderFactory;
import com.github.dyna4jdbc.internal.common.util.classpath.DefaultClassLoaderFactory;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream.AbortHandler;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

public class DefaultScriptEngineConnection extends AbstractConnection implements OutputCapturingScriptExecutor {

    private static final Logger LOGGER = Logger.getLogger(DefaultScriptEngineConnection.class.getName());
    
    private final Object lockObject = new Object();

    private final ScriptEngine engine;
    private final IOHandlerFactory ioHandlerFactory;

    private final ColumnHandlerFactory columnHandlerFactory;
    private final Configuration configuration;
    
    private AtomicReference<AbortableOutputStream.AbortHandler> streamAbortHandlerRef = new AtomicReference<>();

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

        ConfigurationFactory configurationFactory = DefaultConfigurationFactory.getInstance();
        this.configuration =
                configurationFactory.newConfigurationFromParameters(configurationString, properties);

        ClassLoaderFactory classloaderFactory = DefaultClassLoaderFactory.getInstance();
        
        this.engine = loadEngineByName(engineName, this.configuration, classloaderFactory);
        
        this.columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(configuration);
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);

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
    protected final Statement createStatementInternal() throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);

        return new OutputHandlingStatement<>(this, outputHandlerFactory, this);
    }

    @Override
    protected final PreparedStatement prepareStatementInternal(String script) throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);

        return new OutputHandlingPreparedStatement<>(script, this, outputHandlerFactory, this);
    }


    @Override
    public final void executeScriptUsingStreams(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        AbortHandler abortHandler = new AbortableOutputStream.AbortHandler();

        executeScriptUsingAbortableStreams(script,
                variables,
                new AbortableOutputStream(stdOutputStream, abortHandler),
                new AbortableOutputStream(errorOutputStream, abortHandler),
                abortHandler);
    }

    //CHECKSTYLE.OFF: DesignForExtension
    protected void executeScriptUsingAbortableStreams(String script, Map<String, Object> variables,
            AbortableOutputStream stdOutputStream, AbortableOutputStream errorOutputStream, AbortHandler abortHandler)
                    throws ScriptExecutionException {

        synchronized (lockObject) {
            streamAbortHandlerRef.set(abortHandler);

            /* We synchronize so that the execution of two concurrently commenced Statements cannot interfere
             * with each other: remember that ScriptEngines store state and hence are NOT thread-safe.
             * By synchronizing here, we basically implement a mutual exclusion policy for the ScriptEngine.
             */
            ScriptContext engineContext = engine.getContext();
            Bindings bindings = engineContext.getBindings(ScriptContext.ENGINE_SCOPE);

            Writer originalWriter = engineContext.getWriter();
            Writer originalErrorWriter = engineContext.getErrorWriter();

            try (PrintWriter outputPrintWriter = getIoHandlerFactory().newPrintWriter(stdOutputStream, true);
                 PrintWriter errorPrintWriter = getIoHandlerFactory().newPrintWriter(errorOutputStream, true)) {

                engineContext.setWriter(outputPrintWriter);
                engineContext.setErrorWriter(errorPrintWriter);

                applyVariablesToEngineScope(variables, bindings);

                engine.eval(script);


            } catch (ScriptException e) {
                throw new ScriptExecutionException(e);

            } finally {

                removeVariablesFromEngineScope(variables, bindings);

                engineContext.setWriter(originalWriter);
                engineContext.setErrorWriter(originalErrorWriter);

                streamAbortHandlerRef.set(null);
            }
        } // end of synchronized (lockObject) block
    }
    //CHECKSTYLE.ON: DesignForExtension

    private void applyVariablesToEngineScope(Map<String, Object> variables, Bindings bindings) {
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();


                bindings.put(key, value);
            }
        }
    }

    private void removeVariablesFromEngineScope(Map<String, Object> variables, Bindings bindings) {
        if (variables != null) {
            for (String key : variables.keySet()) {


                bindings.remove(key);
            }
        }
    }

    protected final ScriptEngine getEngine() {
        return engine;
    }

    protected final IOHandlerFactory getIoHandlerFactory() {
        return ioHandlerFactory;
    }

    @Override
    public final void cancel() throws CancelException {
        try {
            /* We read this.streamAbortHandler without synchronizing on (lockObject)
             * otherwise a ScriptEngine stuck on spinning a user script would prevent
             * the cancel request acquiring the monitor, and hence ever reaching the
             * AbortHandler.
             */
            AbortHandler abortHandler = this.streamAbortHandlerRef.get();
            if (abortHandler == null) {
                throw JDBCError.CANCEL_FAILED.raiseUncheckedException(
                        "No running statement found.");
            }

            abortHandler.abort();
        } catch (IllegalStateException ise) {
            throw JDBCError.CANCEL_REQUESTED_ALREADY.raiseUncheckedException(
                    ise, "Cancellation requested already.");
        }
    }
}

