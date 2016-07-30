package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream.AbortHandler;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.io.PrintWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;

// TODO: remove this work-around class, once Renjin ScriptEngin is fixed
public final class RenjinScriptEngineConnection extends DefaultScriptEngineConnection {

    private final Object lockObject = new Object();
    
    public RenjinScriptEngineConnection(String parameters, Properties properties) throws SQLException,
            MisconfigurationException {
        super(parameters, properties);
    }

    protected void executeScriptUsingAbortableStreams(String script, Map<String, Object> variables,
            AbortableOutputStream stdOutputStream, AbortableOutputStream errorOutputStream, AbortHandler abortHandler)
                    throws ScriptExecutionException {

        synchronized (lockObject) {
            getStreamAbortHandlerRef().set(abortHandler);

            /* We synchronize so that the execution of two concurrently commenced Statements cannot interfere
             * with each other: remember that ScriptEngines store state and hence are NOT thread-safe.
             * By synchronizing here, we basically implement a mutual exclusion policy for the ScriptEngine.
             */
            ScriptContext engineContext = getEngine().getContext();
            Bindings bindings = getEngine().getBindings(ScriptContext.ENGINE_SCOPE);

            Writer originalWriter = engineContext.getWriter();
            Writer originalErrorWriter = engineContext.getErrorWriter();

            try (PrintWriter outputPrintWriter = getIoHandlerFactory().newPrintWriter(stdOutputStream, true);
                 PrintWriter errorPrintWriter = getIoHandlerFactory().newPrintWriter(errorOutputStream, true)) {

                engineContext.setWriter(outputPrintWriter);
                engineContext.setErrorWriter(errorPrintWriter);

                applyVariablesToEngineScope(variables, bindings);

                getEngine().eval(script);


            } catch (ScriptException e) {
                throw new ScriptExecutionException(e);

            } finally {

                removeVariablesFromEngineScope(variables, bindings);

                engineContext.setWriter(originalWriter);
                engineContext.setErrorWriter(originalErrorWriter);

                getStreamAbortHandlerRef().set(null);
            }
        } // end of synchronized (lockObject) block
    }
}
