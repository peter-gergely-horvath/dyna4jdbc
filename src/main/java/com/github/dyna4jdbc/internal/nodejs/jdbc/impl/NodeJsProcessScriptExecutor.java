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

 
package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.ScriptExecutor;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutor;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLWarning;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


class NodeJsProcessScriptExecutor extends DefaultExternalProcessScriptExecutor {

    private static final String NODE_PROCESS_NAME = "node";
    private static final String NODE_COMMAND_LINE_ARGUMENT = "-e";

    private static final String NODE_EXIT_COMMAND = "process.exit();";

    private static final Logger LOGGER = Logger.getLogger(NodeJsProcessScriptExecutor.class.getName());


    private final String replInitScript;
    private final Configuration configuration;

    
    NodeJsProcessScriptExecutor(Configuration configuration, String replInitScript, SQLWarningSink warningSink) {
        super(configuration, warningSink);
        this.replInitScript = replInitScript;
        this.configuration = configuration;
    }

    @Override
    public void executeScript(String script, Map<String, Object> variables,
                              OutputStream stdOutOutputStream, OutputStream errorOutputStream)
            throws ScriptExecutionException {
        
        VariableSetScriptInvoker variableSetter = new VariableSetScriptInvoker(configuration, this);
        
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                variableSetter.setVariable(key, value);
            }
        }

        super.executeScript(script.replace("\n",  " ").replace("\r",  " "),
                variables, stdOutOutputStream, errorOutputStream);
    }

    private static final class VariableSetScriptInvoker extends AutoGeneratedScriptHandler {

        VariableSetScriptInvoker(Configuration configuration, ScriptExecutor scriptExecutor) {
            super(configuration, scriptExecutor);
        }

        private void setVariable(String variableName, Object variableValue) {

            String stringRepresentation = JavaScriptVariableConverter.convertToString(variableValue);

            String variableSetScript = String.format("%s = %s;", variableName, stringRepresentation);

            try {
                this.invokeScript(variableSetScript);

            } catch (ScriptExecutionException ex) {
                throw JDBCError.NODE_JS_INTEGRATION_ERROR.raiseUncheckedException(
                        String.format("Failed to set variable '%s' to '%s'",
                                variableName, stringRepresentation));
            }
        }

        @Override
        protected void onSingleWarning(String script, SQLWarning warning) {
            throw JDBCError.NODE_JS_INTEGRATION_ERROR.raiseUncheckedException(
                    warning,
                    "stdERR write while invoking dyna4JDBC auto-generated JavaScript intended to set a variable. "
                    + "Script is: " + script);
        }

        @Override
        protected void onMultipleWarnings(String script, List<SQLWarning> warningList) {
            throw JDBCError.NODE_JS_INTEGRATION_ERROR.raiseUncheckedExceptionWithSuppressed(warningList,
                    "stdERR write while invoking dyna4JDBC auto-generated JavaScript intended to set a variable. "
                    + "Script is: " + script);
        }
    }

    private final class ExitNodeJsScriptInvoker extends AutoGeneratedScriptHandler {

        ExitNodeJsScriptInvoker(Configuration configuration, ScriptExecutor scriptExecutor) {
            super(configuration, scriptExecutor);
        }

        private void exitNodeJsProcess() {

            try {
                this.invokeScript(NODE_EXIT_COMMAND);

            } catch (ScriptExecutionException ex) {
                throw JDBCError.NODE_JS_INTEGRATION_ERROR.raiseUncheckedException(
                        String.format("Failed to request Node.js exit with: '%s'", NODE_EXIT_COMMAND));
            }
        }

        @Override
        protected void onSingleWarning(String script, SQLWarning warning) {
            NodeJsProcessScriptExecutor.this.addWarning(
                    new SQLWarning("Script failed: " + script, warning));
        }

        @Override
        protected void onMultipleWarnings(String script, List<SQLWarning> warningList) {

            SQLWarning warning = new SQLWarning("Script failed: " + script);
            for (Throwable suppressed : warningList) {
                warning.addSuppressed(suppressed);
            }

            NodeJsProcessScriptExecutor.this.addWarning(
                    new SQLWarning("Script failed: " + script, warning));

        }
    }


    @Override
    protected void onProcessNotRunningBeforeDispatch(String script) throws ScriptExecutionException {
        throw new ScriptExecutionException(
                "Node.js process exited unexpectedly: Cannot execute script!", script);

    }


    @Override
    protected Process createProcess(String script, Map<String, Object> variables) throws IOException {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(Arrays.asList(NODE_PROCESS_NAME, NODE_COMMAND_LINE_ARGUMENT, replInitScript));

            if (variables != null) {

                Map<String, String> environment = processBuilder.environment();

                variables.entrySet().forEach(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();

                    String valueString = String.valueOf(value);

                    environment.put(key, valueString);
                });
            }

            return processBuilder.start();
        } catch (IOException ioEx) {
            throw JDBCError.NODE_JS_INTEGRATION_ERROR.raiseUncheckedException(ioEx,
                    String.format("Failed to launch Node.js process \"%s\": %s",
                            NODE_PROCESS_NAME, ioEx.getMessage()));
        }
    }

    @Override
    public final void close() {

        try {
            ExitNodeJsScriptInvoker exitCommandInvoker = new ExitNodeJsScriptInvoker(configuration, this);

            exitCommandInvoker.exitNodeJsProcess();

        } catch (RuntimeException ex) {

            LOGGER.log(Level.WARNING, ex, () ->
                    "Exception shutting down Node.js process gracefully; will fall-back to OS kill");


            throw ex;

        } finally {
            super.close();
        }
    }

}
