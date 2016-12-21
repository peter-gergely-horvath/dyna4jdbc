/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
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

import java.io.OutputStream;
import java.io.PrintWriter;
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

    private volatile ProcessManager processRunner;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    private DefaultIOHandlerFactory ioHandlerFactory; 
    
    private final boolean skipFirstLine;
    private final Configuration configuration;
    private final Pattern endOfDataPattern;

    private long expirationIntervalMs;

    public DefaultExternalProcessScriptExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.skipFirstLine = configuration.getSkipFirstLine();
        this.endOfDataPattern = configuration.getEndOfDataPattern();
        this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
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

            if (this.processRunner != null && !this.processRunner.isProcessRunning()) {
                onProcessNotRunningBeforeDispatch(script);
            }

            if (this.processRunner == null) {
                this.processRunner = createProcessManager(script, variables);
            } else {
                this.processRunner.writeToStandardInput(script);
            }

            Future<Void> standardOutFuture = getExecutorService().submit(
                    stdOutWatcher(outputPrintWriter, processRunner));

            Future<Void> standardErrorFuture = getExecutorService().submit(
                    stdErrWatcher(errorPrintWriter, processRunner));

            standardOutFuture.get();
            standardErrorFuture.get();

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

    protected ProcessManager createProcessManager(String script, Map<String, Object> variables)
            throws ProcessExecutionException {
        return ProcessManager.start(script, variables, getConfiguration(), getExecutorService());
    }

    protected void onProcessNotRunningBeforeDispatch(String script) throws ScriptExecutionException {
        // allows the process to be re-initialize
        this.processRunner = null;
    }
    //CHECKSTYLE.ON

    private Callable<Void> stdOutWatcher(final PrintWriter outputPrintWriter, final ProcessManager currentProcess) {
        return () -> {

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
        };
    }

    private Callable<Void> stdErrWatcher(final PrintWriter errorPrintWriter, final ProcessManager currentProcess) {
        return () -> {

            long expirationTime = System.currentTimeMillis() + expirationIntervalMs;

            while (System.currentTimeMillis() < expirationTime) {

                String outputCaptured = currentProcess.pollStandardError(
                        DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

                if (outputCaptured == null) {

                    if (currentProcess.isStandardErrorReachedEnd()) {
                        break;
                    }

                } else {

                    expirationTime = System.currentTimeMillis() + expirationIntervalMs;
                    errorPrintWriter.println(outputCaptured);
                }
            }

            return null;
        };
    }

    @Override
    public final void close() {
        try {
            abortProcessIfRunning();
        } finally {
            getExecutorService().shutdownNow();
        }
    }

    @Override
    public final void cancel() throws CancelException {
        abortProcessIfRunning();
    }

    private void abortProcessIfRunning() {
        ProcessManager currentProcess = processRunner;
        if (currentProcess != null
                && currentProcess.isProcessRunning()) {
            currentProcess.terminateProcess();
            processRunner = null;
        }
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }

}
