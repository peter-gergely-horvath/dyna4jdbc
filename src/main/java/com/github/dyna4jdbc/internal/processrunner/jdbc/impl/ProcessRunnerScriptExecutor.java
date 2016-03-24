package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;

public class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

	private ExecutorService executorService;


	private final Object lock = new Object();


	private Process process;
	private PrintWriter processInputWriter;


	private BlockingQueue<String> stdOutQueue;
	private BlockingQueue<String> stdErrQueue;

	@Override
	public void executeScriptUsingCustomWriters(String script, Writer outWriter, Writer errorWriter)
			throws ScriptExecutionException {

		try {

			PrintWriter outputPrintWriter = new PrintWriter(new BufferedWriter(outWriter));

			synchronized (lock) {
				if(process == null || ! process.isAlive()) {
					foobar(script);
					
				} else {

					processInputWriter.println(script);
					processInputWriter.flush();
				}

				Thread.sleep(1000);

				while (! stdOutQueue.isEmpty()) {

					String pollResult = stdOutQueue.poll(10, TimeUnit.SECONDS);
					if(pollResult == null) {
						stdErrQueue.poll(10, TimeUnit.SECONDS);
					}
					outputPrintWriter.println(pollResult);
				}

				outputPrintWriter.close();
			}

		} catch (IOException e) {
			throw new ScriptExecutionException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void foobar(String excuteAsCommand) throws IOException {

		try {
			process = Runtime.getRuntime().exec(excuteAsCommand);
			if(executorService != null) {
				executorService.shutdown();
			}
			
			executorService = Executors.newCachedThreadPool();

			processInputWriter = new PrintWriter(new BufferedWriter(new PrintWriter(process.getOutputStream())));
			
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

			stdOutQueue = new LinkedBlockingQueue<>();
			stdErrQueue = new LinkedBlockingQueue<>();
			
			executorService.submit(new BufferedReaderToBlockingQueueRunnable(stdOut, stdOutQueue, cyclicBarrier));
			executorService.submit(new BufferedReaderToBlockingQueueRunnable(stdErr, stdErrQueue, cyclicBarrier));


			cyclicBarrier.await(10, TimeUnit.SECONDS);
			
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (BrokenBarrierException | TimeoutException  e) {
			throw new RuntimeException(e);
		}
	}

	private class BufferedReaderToBlockingQueueRunnable implements Runnable
	{
		private final BufferedReader bufferedReader;
		private final BlockingQueue<String> blockingQueue;
		private CyclicBarrier cyclicBarrier;

		private BufferedReaderToBlockingQueueRunnable(BufferedReader bufferedReader, BlockingQueue<String> blockingQueue, CyclicBarrier cyclicBarrier) {
			this.bufferedReader = bufferedReader;
			this.blockingQueue = blockingQueue;
			this.cyclicBarrier = cyclicBarrier;

		}

		@Override
		public void run() {
			try {
				cyclicBarrier.await(10, TimeUnit.SECONDS);

				while(true) {
					String line = bufferedReader.readLine();
					if(line == null) {
						break;
					}
					blockingQueue.put(line);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (BrokenBarrierException | TimeoutException e) {
				e.printStackTrace();
				// abort execution
			} finally {
				try {
					if(bufferedReader != null) {
						bufferedReader.close();
					}
				} catch (IOException e) {
					// swallow any exception raised on close
				}
			}
			
		}

	}
	
	void cancelExecution() {
		synchronized (lock) {
			executorService.shutdown();
			if(process != null) {
				process.destroyForcibly();
			}	
		}

	}


}
