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
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

final class ProcessManager {

    interface ReadListener {
        void onLineRead(String line);
    }

    private static final int DEFAULT_POLL_INTERVAL_MS = 500;

    private static final Logger LOGGER = Logger.getLogger(ProcessManager.class.getName());

    private static final int DEFAULT_TIMEOUT_MILLI_SECONDS = 10_000;

    private final Process processReference;

    private final ExecutorService executorService;
    
    private final PrintWriter processInputWriter;

    private final BlockingQueue<String> standardOutputStreamContentQueue;
    private final BlockingQueue<String> errorStreamContentQueue;

    private final String endOfStreamIndicator;

    private final Configuration configuration;

    private boolean standardOutReachedEnd = false;
    private boolean standardErrorReachedEnd = false;

    
    static ProcessManager newInstance(
            Process process,
            Configuration configuration,
            ExecutorService executorService)
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

            DefaultIOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);


            BufferedReader stdOut = ioHandlerFactory.newBufferedReader(
                    processManager.processReference.getInputStream());

            BufferedReader stdErr = ioHandlerFactory.newBufferedReader(
                    processManager.processReference.getErrorStream());

            Runnable stdOutReader = new BufferedReaderToBlockingQueueRunnable(
                    "StdOut reader", stdOut,
                    processManager.standardOutputStreamContentQueue, cyclicBarrier,
                    processManager.endOfStreamIndicator);

            Runnable stdErrReader = new BufferedReaderToBlockingQueueRunnable(
                    "StdErr reader", stdErr,
                    processManager.errorStreamContentQueue, cyclicBarrier,
                    processManager.endOfStreamIndicator);

            processManager.executorService.execute(stdOutReader);
            processManager.executorService.execute(stdErrReader);

            cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

            return processManager;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessExecutionException("Interrupted", e);

        } catch (BrokenBarrierException | TimeoutException e) {
            throw new ProcessExecutionException(e);
        }
    }

    private ProcessManager(
            Process process,
            Configuration configuration,
            ExecutorService executorService)
                    throws ProcessExecutionException {


            this.configuration = configuration;

            Pattern endOfDataPattern = configuration.getEndOfDataPattern();
            if (endOfDataPattern != null) {
                this.endOfStreamIndicator = endOfDataPattern.toString();
            } else {
                this.endOfStreamIndicator = UUID.randomUUID().toString();
            }

            this.processReference = process;

            IOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);

            processInputWriter = ioHandlerFactory.newPrintWriter(process.getOutputStream(), true);

            standardOutputStreamContentQueue = new LinkedBlockingQueue<>();
            errorStreamContentQueue = new LinkedBlockingQueue<>();

            this.executorService = executorService;
    }

    private void checkProcessState() {

        if (!isProcessRunning()) {
            throw new IllegalStateException("Process is not running");
        }
    }

    Future<Void> submitReadTaskForStdErr(ReadListener onStdErr) {
        return executorService.submit(
                new StdErrorWatcher(configuration, onStdErr));
    }

    Future<Void> submitReadTaskForStdOut(ReadListener onStdOut) {
        return executorService.submit(
                new StdOutWatcher(configuration, onStdOut));
    }

    boolean isProcessRunning() {
        return processReference.isAlive();
    }

    void terminateProcess() {
        checkProcessState();

        processReference.destroyForcibly();
    }

    private boolean isStandardOutReachedEnd() {
        return standardOutReachedEnd;
    }

    private String pollStandardOutput(long timeout, TimeUnit unit) throws IOException {

        try {
            String result = standardOutputStreamContentQueue.poll(timeout, unit);

            if (endOfStreamIndicator.equals(result)) {
                this.standardOutReachedEnd = true;
                result = null;
            }

            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    private boolean isStandardErrorReachedEnd() {
        return standardErrorReachedEnd;
    }

    private String pollStandardError(long timeout, TimeUnit unit) throws IOException {

        try {
            String result = errorStreamContentQueue.poll(timeout, unit);

            if (endOfStreamIndicator.equals(result)) {
                this.standardErrorReachedEnd = true;
                result = null;
            }

            return result;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    void writeToStandardInput(String string) {

        checkProcessState();

        processInputWriter.println(string);
        processInputWriter.flush();
    }

    private static final class BufferedReaderToBlockingQueueRunnable implements Runnable {
        private final String identifier;
        private final BufferedReader bufferedReader;
        private final BlockingQueue<String> blockingQueue;
        private CyclicBarrier cyclicBarrier;
        private final String endOfStreamIndicator;

        private BufferedReaderToBlockingQueueRunnable(String identifier, BufferedReader bufferedReader,
                                                      BlockingQueue<String> blockingQueue,
                                                      CyclicBarrier cyclicBarrier,
                                                      String endOfStreamIndicator) {

            this.identifier = identifier;
            this.bufferedReader = bufferedReader;
            this.blockingQueue = blockingQueue;
            this.cyclicBarrier = cyclicBarrier;
            this.endOfStreamIndicator = endOfStreamIndicator;
        }

        @Override
        public void run() {
            try {
                cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

                while (!Thread.currentThread().isInterrupted()) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        blockingQueue.put(endOfStreamIndicator);
                        break;
                    }
                    blockingQueue.put(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException e) {
                LOGGER.warning("CyclicBarrier is broken: " + identifier);
                e.printStackTrace();
                // abort execution
            } catch (TimeoutException e) {
                LOGGER.warning("CyclicBarrier timeout: " + identifier);
                e.printStackTrace();
                // abort execution
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    LOGGER.warning("IOException closing bufferedReader in: " + identifier);
                    e.printStackTrace();
                    // swallow any exception raised on close
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

    private final class StdErrorWatcher implements Callable<Void> {

        private final Pattern endOfDataPattern;
        private final ReadListener listener;
        private final long expirationIntervalMs;

        StdErrorWatcher(Configuration configuration, ReadListener listener) {
            this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
            this.endOfDataPattern = configuration.getEndOfDataPattern();
            this.listener = listener;

        }

        @Override
        public Void call() throws Exception {
            long expirationTime = System.currentTimeMillis() + expirationIntervalMs;

            while (System.currentTimeMillis() < expirationTime) {

                String outputCaptured = ProcessManager.this.pollStandardError(
                        DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

                if (outputCaptured == null) {

                    if (ProcessManager.this.isStandardErrorReachedEnd()
                            || ProcessManager.this.isStandardOutReachedEnd()) {
                        break;
                    }

                } else {

                    if (endOfDataPattern != null
                            && endOfDataPattern.matcher(outputCaptured).matches()) {
                        break;
                    }

                    expirationTime = System.currentTimeMillis() + expirationIntervalMs;
                    listener.onLineRead(outputCaptured);
                }
            }

            return null;
        }

    }

    private final class StdOutWatcher implements Callable<Void> {

        private final boolean skipFirstLine;
        private final Pattern endOfDataPattern;
        private final ReadListener listener;
        private final long expirationIntervalMs;

        StdOutWatcher(Configuration configuration, ReadListener listener) {
            this.expirationIntervalMs = configuration.getExternalCallQuietPeriodThresholdMs();
            this.skipFirstLine = configuration.getSkipFirstLine();
            this.endOfDataPattern = configuration.getEndOfDataPattern();
            this.listener = listener;

        }

        @Override
        public Void call() throws Exception {
            long expirationTime = System.currentTimeMillis() + expirationIntervalMs;

            boolean firstLine = true;

            while (System.currentTimeMillis() < expirationTime) {

                String outputCaptured = ProcessManager.this.pollStandardOutput(
                        DEFAULT_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS);

                if (outputCaptured == null) {

                    if (ProcessManager.this.isStandardOutReachedEnd()) {
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

                    listener.onLineRead(outputCaptured);
                }
            }

            return null;
        }

    }
}
