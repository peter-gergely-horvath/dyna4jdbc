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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLWarning;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

final class ProcessManager {

    interface ReadListener {
        void onLineRead(String line);
    }

    private static final Logger LOGGER = Logger.getLogger(ProcessManager.class.getName());

    private static final long DEFAULT_POLL_INTERVAL_MS = 500;
    private static final long DEFAULT_TIMEOUT_MILLI_SECONDS = 10_000;

    private static final String END_OF_STREAM_INDICATOR_TOKEN = UUID.randomUUID().toString();


    private final Process processReference;


    private final ExecutorService executorService;


    private final PrintWriter processInputWriter;

    private final BlockingQueue<String> standardOutputStreamContentQueue;
    private final BlockingQueue<String> errorStreamContentQueue;

    private final Configuration configuration;


    static ProcessManager newInstance(
            Process process,
            Configuration configuration,
            ExecutorService executorService,
            SQLWarningSink warningSink)
            throws ProcessExecutionException {

        try {
            ProcessManager processManager = new ProcessManager(process, configuration, executorService);

            final int partiesToWait = 3;
            /*
            CyclicBarrier should wait for three parties:
                1.) Current thread
                2.) stdOutReader thread
                3.) stdErrReader thread
             */
            CyclicBarrier cyclicBarrier = new CyclicBarrier(partiesToWait);

            IOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);

            BufferedReader stdOut = ioHandlerFactory.newBufferedReader(
                    processManager.processReference.getInputStream());

            BufferedReader stdErr = ioHandlerFactory.newBufferedReader(
                    processManager.processReference.getErrorStream());

            Runnable stdOutReader = new BufferedReaderToBlockingQueueRunnable(
                    "StdOut reader", stdOut,
                    processManager.standardOutputStreamContentQueue, cyclicBarrier, warningSink);

            Runnable stdErrReader = new BufferedReaderToBlockingQueueRunnable(
                    "StdErr reader", stdErr,
                    processManager.errorStreamContentQueue, cyclicBarrier, warningSink);

            processManager.executorService.execute(stdOutReader);
            processManager.executorService.execute(stdErrReader);

            cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

            return processManager;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessExecutionException("Interrupted", e);

        } catch (BrokenBarrierException | TimeoutException e) {
            process.destroyForcibly();

            throw new ProcessExecutionException(
                    "At least one process reader thread failed to initialize: process destroyed forcibly", e);
        }
    }

    private ProcessManager(Process process, Configuration configuration, ExecutorService executorService)
            throws ProcessExecutionException {

        this.configuration = configuration;
        this.processReference = process;

        IOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);
        processInputWriter = ioHandlerFactory.newPrintWriter(process.getOutputStream(), true);

        standardOutputStreamContentQueue = new LinkedBlockingQueue<>();
        errorStreamContentQueue = new LinkedBlockingQueue<>();

        this.executorService = executorService;
    }

    Future<Void> submitReadTaskForStdOut(ReadListener onStdOut) {
        return executorService.submit(
                new OutputListenerDispatcher(standardOutputStreamContentQueue,
                        configuration.getSkipFirstLine(), configuration, onStdOut));
    }

    Future<Void> submitReadTaskForStdErr(ReadListener onStdErr) {
        return executorService.submit(
                new OutputListenerDispatcher(errorStreamContentQueue,
                        false, configuration, onStdErr));
    }

    boolean isProcessRunning() {
        return processReference.isAlive();
    }

    void terminateProcess() {
        processReference.destroyForcibly();
    }

    void writeToStandardInput(String string) {
        processInputWriter.println(string);
        processInputWriter.flush();
    }

    private static final class BufferedReaderToBlockingQueueRunnable implements Runnable {
        private final String identifier;
        private final BufferedReader bufferedReader;
        private final BlockingQueue<String> blockingQueue;
        private CyclicBarrier cyclicBarrier;
        private final SQLWarningSink warningSink;

        private BufferedReaderToBlockingQueueRunnable(String identifier,
                                                      BufferedReader bufferedReader,
                                                      BlockingQueue<String> blockingQueue,
                                                      CyclicBarrier cyclicBarrier, SQLWarningSink warningSink) {

            this.identifier = identifier;
            this.bufferedReader = bufferedReader;
            this.blockingQueue = blockingQueue;
            this.cyclicBarrier = cyclicBarrier;
            this.warningSink = warningSink;
        }

        @Override
        public void run() {
            try {
                cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

                while (!Thread.currentThread().isInterrupted()) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        blockingQueue.put(END_OF_STREAM_INDICATOR_TOKEN);
                        break;
                    }
                    blockingQueue.put(line);
                }
            } catch (IOException ioEx) {
                LOGGER.log(Level.WARNING, ioEx, () -> "IOException in " + this.identifier);

                warningSink.onSQLWarning(new SQLWarning(ioEx));

            } catch (BrokenBarrierException bbe) {
                LOGGER.log(Level.SEVERE, bbe, () -> "BrokenBarrierException in " + this.identifier);

                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(bbe,
                        "Caught BrokenBarrierException in " + this.identifier);

            } catch (TimeoutException te) {
                LOGGER.log(Level.SEVERE, te, () -> "TimeoutException in " + this.identifier);

                warningSink.onSQLWarning(new SQLWarning(te));

                throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(te,
                        "Caught TimeoutException in " + this.identifier);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    // should NEVER happen
                    LOGGER.log(Level.WARNING, "Ignored exception thrown while closing BufferedReader", e);
                }
            }

        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("BufferedReaderToBlockingQueueRunnable [identifier=");
            builder.append(identifier);
            builder.append(",\nblockingQueue=");
            builder.append(blockingQueue);
            builder.append("]");
            return builder.toString();
        }


    }

    private static final class OutputListenerDispatcher implements Callable<Void> {

        private final BlockingQueue<String> queueToPoll;
        private final boolean skipFirstLine;
        private final Pattern endOfDataPattern;
        private final ReadListener listener;
        private final long expirationIntervalMs;


        OutputListenerDispatcher(
                BlockingQueue<String> queueToPoll,
                boolean skipFirstLine,
                Configuration configuration,
                ReadListener listener) {
            this.queueToPoll = queueToPoll;
            this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
            this.skipFirstLine = skipFirstLine;
            this.endOfDataPattern = configuration.getEndOfDataPattern();
            this.listener = listener;

        }

        @Override
        public Void call() throws Exception {

            boolean firstLine = true;
            long expirationTime;
            do {

                String outputRead = queueToPoll.poll(DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

                if (outputRead == null || outputRead.equals(END_OF_STREAM_INDICATOR_TOKEN)
                        || endOfDataPattern != null && endOfDataPattern.matcher(outputRead).matches()) {
                    break; // reached end of stream: break out of the loop

                } else {

                    expirationTime = System.currentTimeMillis() + expirationIntervalMs;

                    if (skipFirstLine && firstLine) {
                        firstLine = false;
                        continue;
                    }

                    listener.onLineRead(outputRead);
                }
            } while (System.currentTimeMillis() < expirationTime);

            return null;
        }

    }
}
