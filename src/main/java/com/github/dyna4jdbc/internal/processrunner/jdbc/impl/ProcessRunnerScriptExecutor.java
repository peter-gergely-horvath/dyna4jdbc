package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

public final class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

    private static final int MINIMAL_POLL_INTERVAL_MS = 5;
    private static final int DEFAULT_POLL_INTERVAL_MS = 50;
    private static final int WAIT_BEFORE_CONSUMING_OUTPUT_MS = 1000;

    private final AtomicReference<ProcessRunner> processRunner = new AtomicReference<>();
    private DefaultIOHandlerFactory ioHandlerFactory; 
    
    private final boolean skipFirstLine;
    private final Configuration configuration;
    private final Pattern endOfDataPattern;

    public ProcessRunnerScriptExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.skipFirstLine = configuration.getSkipFirstLine();
        this.endOfDataPattern = configuration.getEndOfDataPattern();
        ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
    }

    @Override
    public void executeScriptUsingStreams(
            String script,
            OutputStream stdOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        // TODO: errorOutputStream is not used!!
        try (PrintWriter outputPrintWriter = ioHandlerFactory.newPrintWriter(stdOutputStream, true)
            /*PrintWriter errorPrintWriter = ioHandlerFactory.newPrintWriter(errorOutputStream, true);*/)  {

            ProcessRunner currentProcess = this.processRunner.get();
            if (currentProcess == null || !currentProcess.isProcessRunning()) {
                currentProcess = ProcessRunner.start(script, configuration);
                this.processRunner.set(currentProcess);
            } else {
                currentProcess.writeToStandardInput(script);
            }

            Thread.sleep(WAIT_BEFORE_CONSUMING_OUTPUT_MS);

            if (skipFirstLine) {
                // skip and discard first result line
                String discardedOutput = currentProcess.pollStandardOutput(
                        MINIMAL_POLL_INTERVAL_MS, TimeUnit.SECONDS);
                System.out.println(discardedOutput);
            }

            String outputCaptured = null;
            String errorCaptured = null;

            while (true) {

                if (currentProcess.isErrorEmpty()) {
                    outputCaptured = currentProcess.pollStandardOutput(
                            MINIMAL_POLL_INTERVAL_MS, TimeUnit.SECONDS);
                } else {


                    if (outputCaptured == null) {
                        outputCaptured = currentProcess.pollStandardError(
                                DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);
                    }
                }

                if (outputCaptured == null) {
                    break;

                } else {

                    if(endOfDataPattern != null
                            && endOfDataPattern.matcher(outputCaptured).matches()) {
                            break;
                    } else {
                        outputPrintWriter.println(outputCaptured);
                    }
                }
            }

        } catch (ProcessExecutionException | IOException e) {
            throw new ScriptExecutionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ScriptExecutionException("Interrupted", e);
        }
    }

    public void close() {

        ProcessRunner currentProcess = this.processRunner.get();
        if (currentProcess != null) {
            currentProcess.terminateProcess();
            this.processRunner.set(null);
        }
    }

    @Override
    public void cancel() throws CancelException {
        close();
    }

}
