package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.logging.Logger;

public final class ProcessRunner {

    private static final Logger LOGGER = Logger.getLogger(ProcessRunner.class.getName());

    private static final int DEFAULT_TIMEOUT_MILLI_SECONDS = 10_000;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private Process processReference;

    private final PrintWriter processInputWriter;

    private final BlockingQueue<String> standardOutputStreamContentQueue;
    private final BlockingQueue<String> errorStreamContentQueue;

    private final String endOfStreamIndicator = UUID.randomUUID().toString();

    private boolean standardOutReachedEnd = false;
    private boolean standardErrorReachedEnd = false;


    static ProcessRunner start(String command, Configuration configuration) throws ProcessExecutionException {

        try {
            Process process = Runtime.getRuntime().exec(command);

            ProcessRunner processRunner = new ProcessRunner(process, configuration);

            final int partiesToWait = 3;
            /*
            CyclicBarrier should wait for three parties:
                1.) Current thread
                2.) stdOutReader thread
                3.) stdErrReader thread
             */
            CyclicBarrier cyclicBarrier = new CyclicBarrier(partiesToWait);

            BufferedReader stdOut = new BufferedReader(
                    new InputStreamReader(processRunner.processReference.getInputStream(),
                            configuration.getConversionCharset()));

            BufferedReader stdErr = new BufferedReader(
                    new InputStreamReader(processRunner.processReference.getErrorStream(),
                            configuration.getConversionCharset()));


            BufferedReaderToBlockingQueueRunnable stdOutReader = new BufferedReaderToBlockingQueueRunnable(
                    String.format("StdOut reader of '%s'", command), stdOut,
                    processRunner.standardOutputStreamContentQueue, cyclicBarrier, processRunner.endOfStreamIndicator);

            BufferedReaderToBlockingQueueRunnable stdErrReader = new BufferedReaderToBlockingQueueRunnable(
                    String.format("StdErr reader of '%s'", command), stdErr,
                    processRunner.errorStreamContentQueue, cyclicBarrier, processRunner.endOfStreamIndicator);

            processRunner.executorService.execute(stdOutReader);
            processRunner.executorService.execute(stdErrReader);

            cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

            return processRunner;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessExecutionException(e);

        } catch (BrokenBarrierException | TimeoutException | IOException e) {
            throw new ProcessExecutionException(e);
        }
    }

    private ProcessRunner(Process process, Configuration configuration) throws ProcessExecutionException {

        DefaultIOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);


            processReference = process;

            processInputWriter = ioHandlerFactory.newPrintWriter(process.getOutputStream(), true);

            standardOutputStreamContentQueue = new LinkedBlockingQueue<>();
            errorStreamContentQueue = new LinkedBlockingQueue<>();
    }

    private void checkProcessState() {

        if (processReference == null) {
            throw new IllegalStateException("Process is not running");
        }
    }

    boolean isProcessRunning() {
        Process process = processReference;
        return process != null && process.isAlive();
    }

    void terminateProcess() {
        checkProcessState();

        executorService.shutdownNow();
        processReference.destroyForcibly();
        processReference = null;
    }

    void discard() {

        executorService.shutdownNow();
        processReference = null;
    }

    boolean isStandardOutReachedEnd() {
        return standardOutReachedEnd;
    }

    String pollStandardOutput(long timeout, TimeUnit unit) throws IOException {

        checkProcessState();

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

    boolean isStandardErrorReachedEnd() {
        return standardErrorReachedEnd;
    }

    String pollStandardError(long timeout, TimeUnit unit) throws IOException {

        checkProcessState();

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

}
