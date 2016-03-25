package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;

public class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

	private final Object lock = new Object();

	private ProcessRunner processRunner;

	private final boolean skipFirstLine;

	public ProcessRunnerScriptExecutor(Configuration configuration) {
		this.skipFirstLine = configuration.getSkipFirstLine();
	}

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

				if (skipFirstLine) {
					// skip and discard first result line
					processRunner.pollStandardOutput(5, TimeUnit.SECONDS);
				}

				do {

					String outputCaptured = null;

					if (processRunner.isErrorEmpty()) {
						outputCaptured = processRunner.pollStandardOutput(5, TimeUnit.SECONDS);
					} else {
						outputCaptured = processRunner.pollStandardOutput(50, TimeUnit.MILLISECONDS);
						
						if(outputCaptured == null) {
							outputCaptured = processRunner.pollStandardError(50, TimeUnit.MILLISECONDS);
						}
						
					}

					if (outputCaptured == null) {
						break;
					} else {
						outputPrintWriter.println(outputCaptured);
					}

				} while (!(processRunner.isOutputEmpty() && processRunner.isErrorEmpty()));
			}

		} catch (ProcessExecutionException | IOException e) {
			throw new ScriptExecutionException(e);
		} catch (InterruptedException e) {
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
