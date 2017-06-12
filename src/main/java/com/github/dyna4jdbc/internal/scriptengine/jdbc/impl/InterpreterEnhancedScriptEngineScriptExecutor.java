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
import com.github.dyna4jdbc.internal.common.util.io.CloseSuppressingOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class InterpreterEnhancedScriptEngineScriptExecutor implements ScriptEngineScriptExecutor {

    private static final Pattern INTERPRETER_COMMAND_PATTERN =
            Pattern.compile("(.*)"              // CAPTURING GROUP 1: content before the interpreter command
                            + "(?:\\n|\\r)*"    // optional newline
                            + "(?:\\s*)"        // optional whitespace(s)
                            + "(?<!jdbc:)"      // NOT a "jdbc:" prefix
                            + "dyna4jdbc:"      // the pattern "dyna4jdbc:"
                            + "([^\\n\\r]+)"    // CAPTURING GROUP 2: the interpreter command
                            + "(.*)",           // CAPTURING GROUP 3: content after the interpreter command
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    private static final int BEFORE_INTERPRETER_COMMAND_GROUP = 1;
    private static final int INTERPRETER_COMMAND_GROUP = 2;
    private static final int AFTER_INTERPRETER_COMMAND_GROUP = 3;

    private final AtomicReference<ScriptEngineScriptExecutor> delegateRef = new AtomicReference<>();

    private final ConcurrentHashMap<String, ScriptEngineScriptExecutor> scriptExecutorMap = new ConcurrentHashMap<>();

    private final ScriptEngineScriptExecutorFactory scriptEngineScriptExecutorFactory;

    InterpreterEnhancedScriptEngineScriptExecutor(ScriptEngineScriptExecutor delegate, Configuration configuration) {

        this.delegateRef.set(delegate);
        this.scriptEngineScriptExecutorFactory = DefaultScriptEngineScriptExecutorFactory.getInstance(configuration);
    }

    @Override
    public void cancel() throws CancelException {
        getDelegate().cancel();
    }

    @Override
    public void setVariables(Map<String, Object> variables) {
        getDelegate().setVariables(variables);
    }

    @Override
    public Map<String, Object> getVariables() {
        return getDelegate().getVariables();
    }

    @Override
    public String getSystemName() {
        return getDelegate().getSystemName();
    }

    @Override
    public String getHumanFriendlyName() {
        return getDelegate().getHumanFriendlyName();
    }

    @Override
    public String getVersion() {
        return getDelegate().getVersion();
    }

    @Override
    public void executeScript(
            String script, Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        if (!INTERPRETER_COMMAND_PATTERN.matcher(script).matches()) {

            // no interpreter command found in input script: simply delegate to current ScriptEngineScriptExecutor

            delegateExecuteScriptUsingStreams(script, variables, stdOutOutputStream, errorOutputStream);

        } else {
                /*
                Implementing the re-binding of the ScriptEngine is somewhat complex here, as
                ScriptExecutor.executeScript CLOSES the streams passed to it.
                Since we might execute this method multiple times (before and after an interpreter command),
                a possible second invocation of executeScript would find the OutputStream in closed
                state.

                To avoid this issue, we pass a close-suppressing OutputStream proxy to the actual execute calls,
                (both to the current ScriptExecutor and the new one as well) and close the actual OutputStream
                after the calls, in the finally blocks implemented here.
                */
                try {
                    try {
                        try {
                            executeScriptsAndInterpreterCommand(script, variables,
                                    new CloseSuppressingOutputStream(stdOutOutputStream),
                                    new CloseSuppressingOutputStream(errorOutputStream));
                        } finally {
                            stdOutOutputStream.close();
                        }
                    } finally {
                        errorOutputStream.close();

                    }
                } catch (IOException ieEx) {
                    throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                            ieEx, "I/O error closing steam");
                }
            }
    }

    private void executeScriptsAndInterpreterCommand(String script, Map<String, Object> variables,
                                                     CloseSuppressingOutputStream csStdOutOutputStream,
                                                     CloseSuppressingOutputStream csErrorOutputStream)
            throws ScriptExecutionException {

        Matcher matcher = INTERPRETER_COMMAND_PATTERN.matcher(script);
        while (matcher.find()) {
            String beforeInterpreterCommand = matcher.group(BEFORE_INTERPRETER_COMMAND_GROUP);
            String interpreterCommandString = matcher.group(INTERPRETER_COMMAND_GROUP);
            String afterInterpreterCommand = matcher.group(AFTER_INTERPRETER_COMMAND_GROUP);

            InterpreterCommand interpreterCommand = Arrays.stream(InterpreterCommand.values())
                    .filter(it -> it.canHandle(interpreterCommandString))
                    .findFirst()
                    .orElseThrow(() ->
                            new ScriptExecutionException(
                                    "Cannot process as interpreter command: " + interpreterCommandString));


            if (beforeInterpreterCommand != null && !"".equals(beforeInterpreterCommand.trim())) {
                /*
                    perform executeScript on the delegate: the attempt to close the OutputStream
                    will be suppressed by the wrapper CloseSuppressingOutputStream
                */
                delegateExecuteScriptUsingStreams(
                        beforeInterpreterCommand, variables, csStdOutOutputStream, csErrorOutputStream);
            }

            String parameters = interpreterCommandString.trim().substring(interpreterCommand.commandName.length());
            interpreterCommand.parseParametersAndExecute(parameters, this);

            if (afterInterpreterCommand != null && !"".equals(afterInterpreterCommand.trim())) {
                /*
                    perform executeScript on the delegate: the attempt to close the OutputStream
                    will be suppressed by the wrapper CloseSuppressingOutputStream
                */
                delegateExecuteScriptUsingStreams(
                        afterInterpreterCommand, variables, csStdOutOutputStream, csErrorOutputStream);
            }
        }
    }

    private void delegateExecuteScriptUsingStreams(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        getDelegate().executeScript(
                script, variables, stdOutOutputStream, errorOutputStream);
    }



    private enum InterpreterCommand {
        SET_SCRIPTENGINE("set ScriptEngine") {
            @Override
            protected void parseParametersAndExecute(
                    String parameters, InterpreterEnhancedScriptEngineScriptExecutor context)
                    throws ScriptExecutionException {

                String scriptEngineName = parameters.trim();
                if ("".equals(scriptEngineName.trim())) {
                    throw new ScriptExecutionException(
                            this.commandName + ": Missing mandatory parameter: ScriptEngineName");
                }


                context.rebindDelegate(scriptEngineName);
            }
        };

        //CHECKSTYLE.OFF: VisibilityModifier: should be visible to enum fields
        protected final String commandName;
        //CHECKSTYLE.ON: VisibilityModifier

        InterpreterCommand(String commandName) {
            this.commandName = commandName;
        }

        protected boolean canHandle(String commandLine) {
            return commandLine != null && commandLine.trim().startsWith(commandName);
        }

        protected abstract void parseParametersAndExecute(
                String parameters, InterpreterEnhancedScriptEngineScriptExecutor context)
                throws ScriptExecutionException;
    }

    private void rebindDelegate(String newScriptExecutorSystemName) {

        ScriptEngineScriptExecutor previousScriptExecutor = getDelegate();

        String systemName = previousScriptExecutor.getSystemName();

        scriptExecutorMap.put(systemName, previousScriptExecutor);

        ScriptEngineScriptExecutor newScriptExecutor = scriptExecutorMap.computeIfAbsent(newScriptExecutorSystemName,

                scriptEngineName -> {
                    try {
                        return this.scriptEngineScriptExecutorFactory.
                                newBasicScriptEngineScriptExecutor(scriptEngineName);
                    } catch (SQLException | MisconfigurationException ex) {
                        throw JDBCError.LOADING_SCRIPTENGINE_FAILED.raiseUncheckedException(ex, scriptEngineName);
                    }
                });

        newScriptExecutor.setVariables(previousScriptExecutor.getVariables());

        if (!delegateRef.compareAndSet(previousScriptExecutor, newScriptExecutor)) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    String.format("failed to compareAndSet(%s, %s)", previousScriptExecutor, newScriptExecutor));
        }
    }

    private ScriptEngineScriptExecutor getDelegate() {
        ScriptEngineScriptExecutor theDelegate = delegateRef.get();
        if (theDelegate == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE
                    .raiseUncheckedException("theDelegate is null");
        }
        return theDelegate;
    }
}
