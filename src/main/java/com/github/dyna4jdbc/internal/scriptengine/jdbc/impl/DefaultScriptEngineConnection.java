package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractConnection;
import com.github.dyna4jdbc.internal.common.jdbc.base.AbstractStatement;
import com.github.dyna4jdbc.internal.common.jdbc.base.AutoClosablePreparedStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingPreparedStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.AbortableOutputStream;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.impl.DefaultColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.ConfigurationFactory;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.DefaultConfigurationFactory;

public class DefaultScriptEngineConnection extends AbstractConnection implements OutputCapturingScriptExecutor {

    private final Object lockObject = new Object();

    private final ScriptEngine engine;
    private final IOHandlerFactory ioHandlerFactory;

    private final ColumnHandlerFactory columnHandlerFactory;
    private final Configuration configuration;

    // CAUTION: these fields HAVE TO BE volatile: see comments in method cancel()
    private volatile AbortableOutputStream abortableOutputStreamForStandardOut;
    private volatile AbortableOutputStream abortableOutputStreamForStandardError;

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

        this.columnHandlerFactory = DefaultColumnHandlerFactory.getInstance(configuration);
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

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(columnHandlerFactory, configuration);

        return new OutputHandlingStatement<>(this, outputHandlerFactory, this);
    }
    
    @Override
    protected final AutoClosablePreparedStatement prepareStatementInternal(String script) throws SQLException {

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

        executeScriptUsingAbortableStreams(script,
                variables,
                new AbortableOutputStream(stdOutputStream),
                new AbortableOutputStream(errorOutputStream));
    }

    //CHECKSTYLE.OFF: DesignForExtension
    protected void executeScriptUsingAbortableStreams(String script, Map<String, Object> variables,
            AbortableOutputStream stdOutputStream, AbortableOutputStream errorOutputStream) 
                    throws ScriptExecutionException {

        synchronized (lockObject) {
            /* We synchronize so that the execution of two concurrently commenced Statements cannot interfere
             * with each other: remember that ScriptEngines store state and hence are NOT thread-safe.
             * By synchronizing here, we basically implement a mutual exclusion policy for the ScriptEngine.
             *
             * Note that we *write* the fields abortableOutputStreamForStandardOut and
             * abortableOutputStreamForStandardError, while holding the monitor of object lockObject, while
             * they are *read*, WITHOUT the lock monitor being held in the method cancel(). For this to work
             * correctly, both fields HAVE TO be _volatile_.
             */

            this.abortableOutputStreamForStandardOut = stdOutputStream;
            this.abortableOutputStreamForStandardError = errorOutputStream;

            ScriptContext engineContext = engine.getContext();

            Writer originalWriter = engineContext.getWriter();
            Writer originalErrorWriter = engineContext.getErrorWriter();

            try (PrintWriter outputPrintWriter = getIoHandlerFactory().newPrintWriter(stdOutputStream, true);
                 PrintWriter errorPrintWriter = getIoHandlerFactory().newPrintWriter(errorOutputStream, true)) {

                engineContext.setWriter(outputPrintWriter);
                engineContext.setErrorWriter(errorPrintWriter);

                applyVariablesToEngineScope(variables, engineContext);

                engine.eval(script);


            } catch (ScriptException e) {
                throw new ScriptExecutionException(e);

            } finally {

                removeVariablesFromEngineScope(variables, engineContext);

                engineContext.setWriter(originalWriter);
                engineContext.setErrorWriter(originalErrorWriter);

                this.abortableOutputStreamForStandardOut = null;
                this.abortableOutputStreamForStandardError = null;
            }
        }
    }
    //CHECKSTYLE.ON: DesignForExtension

    private void applyVariablesToEngineScope(Map<String, Object> variables, ScriptContext engineContext) {
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                engineContext.setAttribute(
                        key, value, ScriptContext.ENGINE_SCOPE);
            }
        }
    }

    private void removeVariablesFromEngineScope(Map<String, Object> variables, ScriptContext engineContext) {
        if (variables != null) {
            for (String key : variables.keySet()) {

                engineContext.removeAttribute(
                        key, ScriptContext.ENGINE_SCOPE);
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
        /* NOTE: the fields abortableOutputStreamForStandardOut and abortableOutputStreamForStandardOut are
         * *written* while the monitor of lockObject is held (synchronized (lockObject)). We read the fields
         * here, without synchronizing on lockObject. This is required, as a thread requesting the execution
         * of retains the monitor of lockObject, until the execution of the script is finished (either
         * normally or abruptly). A ScriptEngine stuck on spinning by a broken user script retains the lock
         * monitor and hence, the request to synchronize on lockObject would be blocked forever (DEADLOCK).
         *
         * To avoid such scenarios, we access fields abortableOutputStreamForStandardOut and
         * abortableOutputStreamForStandardOut without synchronizing on lockObject. For this to work
         * correctly, both fields HAVE TO be _volatile_.
         */
        try {
            if (abortableOutputStreamForStandardOut == null
                    || abortableOutputStreamForStandardError == null) {

                /* This is NOT redundant / the only source for IllegalStateException
                 * in the catch block! This path is executed if cancel() is
                 * called while the _finally_ block in method
                 * executeScriptUsingAbortableStreams is being executed and has
                 * just set the abortable streams to null. Remember: we are NOT
                 * guarded by the lock monitor to prevent deadlocks.
                 */
                throw new IllegalStateException("Cancellation not possible: "
                        + "either operation is finished, "
                        + "or cancellation requested already");
            }

            /* TODO: what if the first one throws exception,
             * but the script is actually spinning
             * on something, which emits output to
             * abortableOutputStreamForStandardError??
             */
            abortableOutputStreamForStandardOut.abort();
            abortableOutputStreamForStandardError.abort();

        } catch (IllegalStateException ise) {
            throw JDBCError.CANCEL_REQUESTED_ALREADY.raiseUncheckedException(
                    ise, "Cancellation requested already.");
        }
    }
}

