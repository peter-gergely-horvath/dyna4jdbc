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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
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
import com.github.dyna4jdbc.internal.common.util.io.DisallowAllWritesOutputStream;
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

        String initScriptPath = this.configuration.getInitScriptPath();
        if (initScriptPath != null) {
            executeInitScript(initScriptPath);
        }
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
    
    private void executeInitScript(String initScriptPath) throws SQLException, MisconfigurationException {
        File initScript = new File(initScriptPath);
        if (!initScript.exists()) {
            throw MisconfigurationException.forMessage("InitScript not found: %s", initScriptPath);
        }

        try {
            byte[] bytesRead = Files.readAllBytes(initScript.toPath());
            String initScriptText = new String(bytesRead, this.configuration.getConversionCharset());

            executeScriptUsingStreams(
                    initScriptText,
                    null,
                    new DisallowAllWritesOutputStream("An init script cannot generate output"),
                    new DisallowAllWritesOutputStream("An init script cannot generate output"));


        } catch (IOException e) {
            throw JDBCError.INITSCRIPT_READ_IO_ERROR.raiseSQLException(initScriptPath);
        } catch (ScriptExecutionException e) {
            throw JDBCError.INITSCRIPT_EXECUTION_EXCEPTION.raiseSQLException(e, initScriptPath);
        }
    }


    @Override
    protected final DatabaseMetaData getMetaDataInternal() throws SQLException {

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
    private void executeScriptUsingAbortableStreams(String script, Map<String, Object> variables,
            AbortableOutputStream stdOutputStream, AbortableOutputStream errorOutputStream, AbortHandler abortHandler)
                    throws ScriptExecutionException {

        synchronized (lockObject) {
            streamAbortHandlerRef.set(abortHandler);

            /* We synchronize so that the execution of two concurrently commenced Statements cannot interfere
             * with each other: remember that ScriptEngines store state and hence are NOT thread-safe.
             * By synchronizing here, we basically implement a mutual exclusion policy for the ScriptEngine.
             */
            ScriptContext engineContext = engine.getContext();
            if (engineContext == null) {
                throw JDBCError.NON_STANDARD_COMPLIANT_SCRIPTENGINE.raiseUncheckedException(
                        "javax.script.ScriptEngine.getContext() returned null");
            }

            Bindings bindings = getBindings(engineContext);

            Writer originalWriter = engineContext.getWriter();
            Writer originalErrorWriter = engineContext.getErrorWriter();

            try (PrintWriter outputPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true);
                 PrintWriter errorPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true)) {

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

    private Bindings getBindings(ScriptContext engineContext) {
        Bindings bindings = engineContext.getBindings(ScriptContext.ENGINE_SCOPE);
        if (bindings == null) {
            // Work-around for Renjin ScriptEngine issue
            bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            if (bindings == null) {
                throw JDBCError.NON_STANDARD_COMPLIANT_SCRIPTENGINE.raiseUncheckedException(
                        "Could not retrieve javax.script.Bindings from ScriptEngine");
            }
        }
        return bindings;
    }

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
    //CHECKSTYLE.ON: DesignForExtension

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
                throw new CancelException("No running statement found to cancel.");
            }

            abortHandler.abort();
        } catch (IllegalStateException ise) {
            throw new CancelException("Cancellation requested already.");
        }
    }
}

