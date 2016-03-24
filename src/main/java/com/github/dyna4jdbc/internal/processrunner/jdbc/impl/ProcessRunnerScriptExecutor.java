package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;

public class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

	private final Object lock = new Object();

	private ProcessRunner processRunner;

	@Override
	public void executeScriptUsingCustomWriters(String script, Writer outWriter, Writer errorWriter)
			throws ScriptExecutionException {

		try (PrintWriter outputPrintWriter = new PrintWriter(outWriter)) {

			synchronized (lock) {

				if (processRunner == null || !processRunner.isProcessRunning()) {
					processRunner = ProcessRunner.start(script);
				} else {
					processRunner.writeToStandardInput(script);
				}

				Thread.sleep(1000);

				do {

					String outputCaptured = processRunner.pollStandardOutput(5, TimeUnit.SECONDS);

					if (outputCaptured == null) {
						outputCaptured = processRunner.pollStandardError(5, TimeUnit.SECONDS);
					}

					if (outputCaptured == null) {
						break;
					} else {
						outputPrintWriter.println(outputCaptured);
					}

				} while (!processRunner.isOutputEmpty());
			}

		} 
		catch (ProcessExecutionException | IOException e) {
			throw new ScriptExecutionException(e);
		} 
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ScriptExecutionException("Interrupted", e);
		}
	}

	void cancelExecution() {
		synchronized (lock) {

			if (processRunner != null) {
				processRunner.terminateProcess();
				processRunner = null;
			}
		}
	}

	public void close() {
		synchronized (lock) {

			if (processRunner != null) {
				processRunner.close();
				processRunner = null;
			}
		}
		
	}
	


}
