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

final class DefaultProcessRunnerScriptExecutor implements ProcessScriptExecutor {

    private static final int DEFAULT_POLL_INTERVAL_MS = 500;

    private volatile ProcessRunner processRunner;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    private DefaultIOHandlerFactory ioHandlerFactory; 
    
    private final boolean skipFirstLine;
    private final Configuration configuration;
    private final Pattern endOfDataPattern;

    private long expirationIntervalMs;

    DefaultProcessRunnerScriptExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.skipFirstLine = configuration.getSkipFirstLine();
        this.endOfDataPattern = configuration.getEndOfDataPattern();
        this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
        this.ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
    }

    @Override
    public void executeScriptUsingStreams(
            String script,
            Map<String, Object> variables,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        try (PrintWriter outputPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true);
            PrintWriter errorPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true))  {

            if (this.processRunner != null && !this.processRunner.isProcessRunning()) {
                this.processRunner = null;
            }

            if (this.processRunner == null) {
                this.processRunner = ProcessRunner.start(script, variables, configuration, executorService);
            } else {
                this.processRunner.writeToStandardInput(script);
            }

            Future<Void> standardOutFuture = executorService.submit(
                    stdOutWatcher(outputPrintWriter, processRunner));

            Future<Void> standardErrorFuture = executorService.submit(
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



    private Callable<Void> stdOutWatcher(final PrintWriter outputPrintWriter, final ProcessRunner currentProcess) {
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

    private Callable<Void> stdErrWatcher(final PrintWriter errorPrintWriter, final ProcessRunner currentProcess) {
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
    public void close() {
        try {
            abortProcessIfRunning();
        } finally {
            executorService.shutdownNow();
        }
    }

    @Override
    public void cancel() throws CancelException {
        abortProcessIfRunning();
    }

    private void abortProcessIfRunning() {
        ProcessRunner currentProcess = processRunner;
        if (currentProcess != null
                && currentProcess.isProcessRunning()) {
            currentProcess.terminateProcess();
            processRunner = null;
        }
    }

}
