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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

public class DefaultExternalProcessScriptExecutor implements ExternalProcessScriptExecutor {

    private static final int DEFAULT_POLL_INTERVAL_MS = 500;

    private volatile ProcessManager processManager;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final DefaultIOHandlerFactory ioHandlerFactory; 
    private final ProcessManagerFactory processManagerFactory;
    
    private final boolean skipFirstLine;
    private final Pattern endOfDataPattern;

    private long expirationIntervalMs;

    public DefaultExternalProcessScriptExecutor(Configuration configuration) {
        this.skipFirstLine = configuration.getSkipFirstLine();
        this.endOfDataPattern = configuration.getEndOfDataPattern();
        this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
        this.processManagerFactory = ProcessManagerFactory.getInstance(configuration, executorService);
    }

    //CHECKSTYLE.OFF: DesignForExtension : incorrect detection of "is not designed for extension"
    @Override
    public void executeScriptUsingStreams(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        try (PrintWriter outputPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true);
            PrintWriter errorPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true))  {

            if (this.processManager != null && !this.processManager.isProcessRunning()) {
                onProcessNotRunningBeforeDispatch(script);
            }

            if (this.processManager == null) {
                Process newProcess = createProcess(script, variables);
                
                this.processManager = processManagerFactory.newProcessManager(newProcess);
            } else {
                this.processManager.writeToStandardInput(script);
            }

            Future<Void> standardOutFuture = executorService.submit(
                    new StdOutWatcher(outputPrintWriter, processManager));

            Future<Void> standardErrorFuture = executorService.submit(
                    new StdErrorWatcher(errorPrintWriter, processManager));

            standardOutFuture.get();
            standardErrorFuture.get();

        } catch (IOException e) {
            throw new ScriptExecutionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Interrupted", e);
        } catch (ExecutionException e) {
            Throwable actualThrowable = e.getCause();
            throw new ScriptExecutionException(actualThrowable);

        } catch (ProcessExecutionException e) {
            throw new ScriptExecutionException(e);
        }
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

    protected void onProcessNotRunningBeforeDispatch(String script) throws ScriptExecutionException {
        // allows the process to be re-initialize
        this.processManager = null;
    }
    //CHECKSTYLE.ON


    private final class StdOutWatcher implements Callable<Void> {

        private final PrintWriter outputPrintWriter;
        private final ProcessManager currentProcess;

        private StdOutWatcher(PrintWriter outputPrintWriter, ProcessManager currentProcess) {
            this.outputPrintWriter = outputPrintWriter;
            this.currentProcess = currentProcess;
        }

        @Override
        public Void call() throws Exception {
            long expirationTime = System.currentTimeMillis() + expirationIntervalMs;

            boolean firstLine = true;

            while (System.currentTimeMillis() < expirationTime) {

                String outputCaptured = currentProcess.pollStandardOutput(
                        DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

                if (outputCaptured == null) {

                    if (currentProcess.isStandardOutReachedEnd()) {
                        break;
                    }

                } else {

                    expirationTime = System.currentTimeMillis() + expirationIntervalMs;

                    if (endOfDataPattern != null
                            && endOfDataPattern.matcher(outputCaptured).matches()) {
                        break;
                    }

                    if (firstLine) {
                        firstLine = false;

                        if (skipFirstLine) {
                            continue;
                        }
                    }

                    outputPrintWriter.println(outputCaptured);
                }
            }

            return null;
        }

    }

    private final class StdErrorWatcher implements Callable<Void> {

        private final PrintWriter errorPrintWriter;
        private final ProcessManager currentProcess;

        private StdErrorWatcher(PrintWriter errorPrintWriter, ProcessManager currentProcess) {
            this.errorPrintWriter = errorPrintWriter;
            this.currentProcess = currentProcess;
        }

        @Override
        public Void call() throws Exception {
            long expirationTime = System.currentTimeMillis() + expirationIntervalMs;

            while (System.currentTimeMillis() < expirationTime) {

                String outputCaptured = currentProcess.pollStandardError(
                        DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

                if (outputCaptured == null) {

                    if (currentProcess.isStandardErrorReachedEnd()
                            || currentProcess.isStandardOutReachedEnd()) {
                        break;
                    }

                } else {

                    if (endOfDataPattern != null
                            && endOfDataPattern.matcher(outputCaptured).matches()) {
                        break;
                    }

                    expirationTime = System.currentTimeMillis() + expirationIntervalMs;
                    errorPrintWriter.println(outputCaptured);
                }
            }

            return null;
        }

    }

    @Override
    public final void close() {
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
