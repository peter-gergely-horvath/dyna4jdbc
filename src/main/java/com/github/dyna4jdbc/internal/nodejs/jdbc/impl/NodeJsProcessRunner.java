package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.util.io.DisallowAllWritesOutputStream;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessScriptExecutor;

final class NodeJsProcessRunner implements ProcessScriptExecutor {

    private final ProcessScriptExecutor delegate;
    
    private static final OutputStream VARIABLE_SET_OUTPUT_STREAM = new DisallowAllWritesOutputStream(
            "Writing to standard output while a variable is being set is unexpected");

    NodeJsProcessRunner(ProcessScriptExecutor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void executeScriptUsingStreams(String script, Map<String, Object> variables,
            OutputStream stdOutOutputStream, OutputStream errorOutputStream) throws ScriptExecutionException {
        
        if (variables != null) {
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                String statement;
                if (value == null) {
                    statement = String.format("%s=null;", key);
                } else if (value instanceof String) {
                    // TODO: quoting??
                    statement = String.format("%s='%s';", key, value);
                } else if (value instanceof Integer) {
                    statement = String.format("%s=%s;", key, value);
                } else if (value instanceof Double) {
                    statement = String.format("%s=%s;", key, value);
                } else if (value instanceof Boolean) {
                    statement = String.format("%s=%s;", key, value);
                } else {
                    statement = String.format("%s='%s';", key, value);
                }
                
                delegate.executeScriptUsingStreams(statement, Collections.emptyMap(), 
                        VARIABLE_SET_OUTPUT_STREAM, VARIABLE_SET_OUTPUT_STREAM);
            }

        }

        delegate.executeScriptUsingStreams(script.replace("\n",  " ").replace("\r",  " "),
                variables, stdOutOutputStream, errorOutputStream);
    }

    @Override
    public void cancel() throws CancelException {
        delegate.cancel();
    }

    @Override
    public void close() {
        delegate.close();
    }
}
