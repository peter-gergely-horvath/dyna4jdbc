package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

public final class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

    private static final int DEFAULT_POLL_INTERVAL_MS = 500;

    private volatile ProcessRunner processRunner;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    private DefaultIOHandlerFactory ioHandlerFactory; 
    
    private final boolean skipFirstLine;
    private final Configuration configuration;
    private final Pattern endOfDataPattern;

    private long expirationIntervalMs;

    public ProcessRunnerScriptExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.skipFirstLine = configuration.getSkipFirstLine();
        this.endOfDataPattern = configuration.getEndOfDataPattern();
        this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
        ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
    }

    @Override
    public void executeScriptUsingStreams(
            String script,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        try (PrintWriter outputPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true);
            PrintWriter errorPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true))  {

            if (this.processRunner == null) {
                this.processRunner = ProcessRunner.start(script, configuration);
            } else if (this.processRunner != null && !this.processRunner.isProcessRunning()) {
                this.processRunner.discard();
                this.processRunner = ProcessRunner.start(script, configuration);
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

    public void close() {
        abortProcessIfRunning();
    }

    @Override
    public void cancel() throws CancelException {
        abortProcessIfRunning();
    }

    private void abortProcessIfRunning() {
        executorService.shutdownNow();

        ProcessRunner currentProcess = processRunner;
        if (currentProcess != null) {
            currentProcess.terminateProcess();
            processRunner = null;
        }
    }

}
