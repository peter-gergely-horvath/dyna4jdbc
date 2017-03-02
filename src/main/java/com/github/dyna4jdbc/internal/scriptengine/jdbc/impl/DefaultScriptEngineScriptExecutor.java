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

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.io.AbortableOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;

import javax.script.*;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

class DefaultScriptEngineScriptExecutor implements ScriptEngineScriptExecutor {

    private final Object lockObject = new Object();

    private final String systemName;
    private final ScriptEngine engine;

    private final IOHandlerFactory ioHandlerFactory;

    private AtomicReference<AbortableOutputStream.AbortHandler> streamAbortHandlerRef = new AtomicReference<>();

    DefaultScriptEngineScriptExecutor(String systemName, ScriptEngine engine, Configuration configuration) {
        this.systemName = systemName;
        this.engine = engine;
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
    }

    @Override
    public void executeScriptUsingStreams(
            String script, Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        AbortableOutputStream.AbortHandler abortHandler = new AbortableOutputStream.AbortHandler();

        executeScriptUsingAbortableStreams(script,
                variables,
                new AbortableOutputStream(stdOutOutputStream, abortHandler),
                new AbortableOutputStream(errorOutputStream, abortHandler),
                abortHandler);
    }

    private void executeScriptUsingAbortableStreams(String script, Map<String, Object> variables,
                                                    AbortableOutputStream stdOutputStream,
                                                    AbortableOutputStream errorOutputStream,
                                                    AbortableOutputStream.AbortHandler abortHandler)
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

    @Override
    public void cancel() throws CancelException {
        try {
            /* We read this.streamAbortHandler without synchronizing on (lockObject)
             * otherwise a ScriptEngine stuck on spinning a user script would prevent
             * the cancel request acquiring the monitor, and hence ever reaching the
             * AbortHandler.
             */
            AbortableOutputStream.AbortHandler abortHandler = streamAbortHandlerRef.get();
            if (abortHandler == null) {
                throw new CancelException("No running statement found to cancel.");
            }

            abortHandler.abort();
        } catch (IllegalStateException ise) {
            throw new CancelException("Cancellation requested already.");
        }
    }

    @Override
    public String getSystemName() {
        return systemName;
    }

    @Override
    public String getHumanFriendlyName() {
        ScriptEngineFactory factory = this.engine.getFactory();

        // graceful handling of cases, where the ScriptEngine returns null
        if (factory != null) {
            String engineName = factory.getEngineName();
            String languageVersion = factory.getLanguageVersion();

            return  String.format("%s (%s)", engineName, languageVersion);
        }

        return null;
    }

    @Override
    public String getVersion() {
        ScriptEngineFactory factory = this.engine.getFactory();

        // graceful handling of cases, where the ScriptEngine returns null
        if (factory != null) {
            return factory.getEngineVersion();
        }

        return null;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, wrapping: %s)",
                super.toString(), systemName, engine);

    }
}
