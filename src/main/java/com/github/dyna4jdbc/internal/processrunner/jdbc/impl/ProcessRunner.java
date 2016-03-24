package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class ProcessRunner {

	private static final Logger LOGGER = Logger.getLogger(ProcessRunner.class.getName());

	private static final int DEFAULT_TIMEOUT_MILLI_SECONDS = 10_000;

	private final Object lock = new Object();

	private ExecutorService executorService;

	private Process process;
	private PrintWriter processInputWriter;

	private BlockingQueue<String> standardOutputStreamContentQueue;
	private BlockingQueue<String> errorStreamContentQueue;

	static ProcessRunner start(String command) throws ProcessExecutionException {
		return new ProcessRunner(command);
	}

	private ProcessRunner(String command) throws ProcessExecutionException {

		try {
			synchronized (lock) {
				Runtime runtime = Runtime.getRuntime();
				process = runtime.exec(command);

				executorService = Executors.newCachedThreadPool();

				processInputWriter = new PrintWriter(new BufferedWriter(new PrintWriter(process.getOutputStream())));

				BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

				CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

				standardOutputStreamContentQueue = new LinkedBlockingQueue<>();
				errorStreamContentQueue = new LinkedBlockingQueue<>();

				executorService.submit(new BufferedReaderToBlockingQueueRunnable("stdandard Out reader", stdOut,
						standardOutputStreamContentQueue, cyclicBarrier));
				executorService.submit(new BufferedReaderToBlockingQueueRunnable("stdandard Error reader", stdErr,
						errorStreamContentQueue, cyclicBarrier));

				cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);
			}

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (BrokenBarrierException | TimeoutException | IOException e) {
			throw new ProcessExecutionException(e);
		}
	}

	private void checkProcessState() {
		synchronized (lock) {
			if (process == null) {
				throw new IllegalStateException("Process is not running");
			}
		}
	}

	boolean isProcessRunning() {
		synchronized (lock) {
			return process != null && process.isAlive();
		}
	}

	void terminateProcess() {
		synchronized (lock) {
			executorService.shutdownNow();
			if (process != null) {
				process.destroyForcibly();
				process = null;
			}
		}
	}
	
	boolean isOutputEmpty() {
		return standardOutputStreamContentQueue.isEmpty();
	}
	
	boolean isErrorEmpty() {
		return errorStreamContentQueue.isEmpty();
	}

	String pollStandardOutput(long timeout, TimeUnit unit) throws IOException {
		try {
			checkProcessState();

			return standardOutputStreamContentQueue.poll(timeout, unit);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(e);
		}
	}
	
	String pollStandardError(long timeout, TimeUnit unit) throws IOException {
		try {
			checkProcessState();

			return errorStreamContentQueue.poll(timeout, unit);
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

	private static class BufferedReaderToBlockingQueueRunnable implements Runnable {
		private final String identifier;
		private final BufferedReader bufferedReader;
		private final BlockingQueue<String> blockingQueue;
		private CyclicBarrier cyclicBarrier;

		private BufferedReaderToBlockingQueueRunnable(String identifier, BufferedReader bufferedReader,
				BlockingQueue<String> blockingQueue, CyclicBarrier cyclicBarrier) {

			this.identifier = identifier;
			this.bufferedReader = bufferedReader;
			this.blockingQueue = blockingQueue;
			this.cyclicBarrier = cyclicBarrier;

		}

		@Override
		public void run() {
			try {
				cyclicBarrier.await(DEFAULT_TIMEOUT_MILLI_SECONDS, TimeUnit.MILLISECONDS);

				while (true) {
					String line = bufferedReader.readLine();
					if (line == null) {
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
	}

	void close() {
		terminateProcess();
	}

}
