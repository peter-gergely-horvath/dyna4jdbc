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


package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLWarning;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

public class DefaultExternalProcessScriptExecutor implements ExternalProcessScriptExecutor {


    private volatile ProcessManager processManager;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final DefaultIOHandlerFactory ioHandlerFactory;
    private final ProcessManagerFactory processManagerFactory;
    private final SQLWarningSink warningSink;


    public DefaultExternalProcessScriptExecutor(Configuration configuration, SQLWarningSink warningSink) {
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
        this.processManagerFactory = ProcessManagerFactory.getInstance(configuration, executorService);
        this.warningSink = warningSink;
    }

    //CHECKSTYLE.OFF: DesignForExtension : incorrect detection of "is not designed for extension"
    @Override
    public void executeScript(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        try (PrintWriter outputPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true);
             PrintWriter errorPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true)) {

            if (this.processManager != null && !this.processManager.isProcessRunning()) {
                onProcessNotRunningBeforeDispatch(script);
            }

            if (this.processManager == null) {
                Process newProcess = createProcess(script, variables);

                this.processManager = processManagerFactory.newProcessManager(newProcess, warningSink);
            } else {
                this.processManager.writeToStandardInput(script);
            }

            Future<Void> standardOutFuture = processManager.submitReadTaskForStdOut(
                    line -> outputPrintWriter.println(line));

            Future<Void> standardErrorFuture = processManager.submitReadTaskForStdErr(
                    line -> errorPrintWriter.println(line));

            standardOutFuture.get();
            standardErrorFuture.get();

        } catch (IOException e) {
            throw new ScriptExecutionException(e, script);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Interrupted", e, script);
        } catch (ExecutionException e) {
            Throwable actualThrowable = e.getCause();
            throw new ScriptExecutionException(actualThrowable, script);

        } catch (ProcessExecutionException e) {
            throw new ScriptExecutionException(e, script);
        }
    }

    protected void onProcessNotRunningBeforeDispatch(String script) throws ScriptExecutionException {
        // allows the process to be re-initialize
        this.processManager = null;
    }

    protected Process createProcess(String script, Map<String, Object> variables) throws IOException {

        String[] variableParameters;

        if (variables == null) {
            variableParameters = null;
        } else {
            List<String> variableDeclarations = new LinkedList<>();
            for (Map.Entry<String, Object> variable : variables.entrySet()) {

                String key = variable.getKey();
                String valueString = String.valueOf(variable.getValue());

                String variableSetting = String.format("%s=%s", key, valueString);
                variableDeclarations.add(variableSetting);
            }

            variableParameters =
                    variableDeclarations.toArray(new String[variableDeclarations.size()]);
        }

        return Runtime.getRuntime().exec(script, variableParameters);
    }
    //CHECKSTYLE.ON

    protected final void addWarning(SQLWarning warning) {
        warningSink.onSQLWarning(warning);
    }


    @Override
    public void close() {
        try {
            abortProcessIfRunning();
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public final void cancel() throws CancelException {
        abortProcessIfRunning();
    }

    private void abortProcessIfRunning() {
        ProcessManager currentProcessManager = processManager;
        if (currentProcessManager != null
                && currentProcessManager.isProcessRunning()) {
            currentProcessManager.terminateProcess();
            processManager = null;
        }
    }

}
