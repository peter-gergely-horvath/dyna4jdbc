package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ProcessRunner {

    private static final Logger LOGGER = Logger.getLogger(ProcessRunner.class.getName());

    private static final int DEFAULT_TIMEOUT_MILLI_SECONDS = 10_000;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final AtomicReference<Process> processReference = new AtomicReference<>();
    private final PrintWriter processInputWriter;

    private final BlockingQueue<String> standardOutputStreamContentQueue;
    private final BlockingQueue<String> errorStreamContentQueue;

    private final String endOfStreamIndicator = UUID.randomUUID().toString();

    static ProcessRunner start(String command) throws ProcessExecutionException {
        return new ProcessRunner(command);
    }

    private ProcessRunner(String command) throws ProcessExecutionException {

        try {
            Runtime runtime = Runtime.getRuntime();

            Process process = runtime.exec(command);

            processReference.set(process);

            processInputWriter = new PrintWriter(new BufferedWriter(new PrintWriter(process.getOutputStream())));

            BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

            standardOutputStreamContentQueue = new LinkedBlockingQueue<>();
            errorStreamContentQueue = new LinkedBlockingQueue<>();

            executorService.execute(new BufferedReaderToBlockingQueueRunnable(
                    String.format("StdOut reader of '%s'", command), stdOut,
                    standardOutputStreamContentQueue, cyclicBarrier));
            executorService.execute(new BufferedReaderToBlockingQueueRunnable(
                    String.format("StdErr reader of '%s'", command), stdErr,
                    errorStreamContentQueue, cyclicBarrier));

            cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProcessExecutionException(e);
        } catch (BrokenBarrierException | TimeoutException | IOException e) {
            throw new ProcessExecutionException(e);
        } finally {
            LOGGER.exiting("ProcessRunner", "<init>");
        }
    }

    private void checkProcessState() {

        Process process = processReference.get();
        if (process == null) {
            throw new IllegalStateException("Process is not running");
        }
    }

    boolean isProcessRunning() {
        Process process = processReference.get();
        return process != null && process.isAlive();
    }

    void terminateProcess() {

        executorService.shutdownNow();
        Process process = processReference.get();
        process.destroyForcibly();
        processReference.set(null);
    }

    boolean isOutputEmpty() {
        String stdOutEntry = standardOutputStreamContentQueue.peek();
        return (stdOutEntry == null ||
                endOfStreamIndicator.equals(stdOutEntry));

    }

    boolean isErrorEmpty() {
        String stdErrorOutputEntry = errorStreamContentQueue.peek();
        return (stdErrorOutputEntry == null ||
                endOfStreamIndicator.equals(stdErrorOutputEntry));
    }

    String pollStandardOutput(long timeout, TimeUnit unit) throws IOException {

        checkProcessState();

        return pollQueue(standardOutputStreamContentQueue, endOfStreamIndicator, timeout, unit);
    }

    String pollStandardError(long timeout, TimeUnit unit) throws IOException {

        checkProcessState();

        return pollQueue(errorStreamContentQueue, endOfStreamIndicator, timeout, unit);
    }

    private static String pollQueue(
            BlockingQueue<String> queue,
            String endOfStreamIndicator,
            long timeout, TimeUnit unit) throws IOException {

        try {
            String result = queue.poll(timeout, unit);

            if (endOfStreamIndicator.equals(result)) {
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

    private class BufferedReaderToBlockingQueueRunnable implements Runnable {
        private final String identifier;
        private final BufferedReader bufferedReader;
        private final BlockingQueue<String> blockingQueue;
        private CyclicBarrier cyclicBarrier;

        private BufferedReaderToBlockingQueueRunnable(String identifier, BufferedReader bufferedReader,
                                                      BlockingQueue<String> blockingQueue,
                                                      CyclicBarrier cyclicBarrier) {

            this.identifier = identifier;
            this.bufferedReader = bufferedReader;
            this.blockingQueue = blockingQueue;
            this.cyclicBarrier = cyclicBarrier;

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
