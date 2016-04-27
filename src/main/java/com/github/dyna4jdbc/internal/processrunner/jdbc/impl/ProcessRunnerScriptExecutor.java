package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;

public class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

	private final AtomicReference<ProcessRunner> processRunner = new AtomicReference<>();

	private final boolean skipFirstLine;

	public ProcessRunnerScriptExecutor(Configuration configuration) {
		this.skipFirstLine = configuration.getSkipFirstLine();
	}

	@Override
	public void executeScriptUsingCustomWriters(String script, Writer outWriter, Writer errorWriter)
			throws ScriptExecutionException {

		try (PrintWriter outputPrintWriter = new PrintWriter(outWriter)) {

			ProcessRunner currentProcess = this.processRunner.get();
			if (currentProcess == null || !currentProcess.isProcessRunning()) {
					currentProcess = ProcessRunner.start(script);
					this.processRunner.set(currentProcess);
				} else {
					currentProcess.writeToStandardInput(script);
				}

				Thread.sleep(1000);

				if (skipFirstLine) {
					// skip and discard first result line
					String discardedOutput = currentProcess.pollStandardOutput(5, TimeUnit.SECONDS);
					System.out.println(discardedOutput);
				}

				String outputCaptured = null;
				
				do {

					if (currentProcess.isErrorEmpty()) {
						outputCaptured = currentProcess.pollStandardOutput(5, TimeUnit.SECONDS);
					} else {
						outputCaptured = currentProcess.pollStandardOutput(50, TimeUnit.MILLISECONDS);
						
						if(outputCaptured == null) {
							outputCaptured = currentProcess.pollStandardError(50, TimeUnit.MILLISECONDS);
						}
					}

					if (outputCaptured == null) {
						break;
					} else {
						outputPrintWriter.println(outputCaptured);
					}

				} while (outputCaptured != null);

		} catch (ProcessExecutionException | IOException e) {
			throw new ScriptExecutionException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ScriptExecutionException("Interrupted", e);
		}
	}

	public void close() {

        ProcessRunner currentProcess = this.processRunner.get();
        if(currentProcess != null) {
            currentProcess.terminateProcess();
            this.processRunner.set(null);
        }
	}

}
