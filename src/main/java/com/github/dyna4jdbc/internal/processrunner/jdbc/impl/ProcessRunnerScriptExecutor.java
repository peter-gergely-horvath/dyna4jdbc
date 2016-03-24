package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;

public class ProcessRunnerScriptExecutor implements OutputCapturingScriptExecutor {

	@Override
	public void executeScriptUsingCustomWriters(String script, Writer outWriter, Writer errorWriter)
			throws ScriptExecutionException {
        
		try {
		    
			PrintWriter outputPrintWriter = new PrintWriter(new BufferedWriter(outWriter));
			
			Process process = Runtime.getRuntime().exec(script);
		 
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	outputPrintWriter.println(line);
		    }
		 
		    reader.close();
		    outputPrintWriter.close();
		 
		} catch (IOException e) {
		    throw new ScriptExecutionException(e);
		}
	}
}
