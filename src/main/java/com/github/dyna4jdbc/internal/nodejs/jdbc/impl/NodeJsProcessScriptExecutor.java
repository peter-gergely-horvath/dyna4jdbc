package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutor;


class NodeJsProcessScriptExecutor extends DefaultExternalProcessScriptExecutor {

    private static final OutputStream VARIABLE_SET_OUTPUT_STREAM = new OutputStream() {

        @Override
        public void write(int b) throws IOException {
            System.err.write(b);
        }
    };
    // TODO: replace with the following:
    /*new DisallowAllWritesOutputStream(
            "Writing to standard output while a variable is being set is unexpected");*/
    
    
    private String replInitScript;

    
    NodeJsProcessScriptExecutor(Configuration configuration, String replInitScript) {
        super(configuration);
        this.replInitScript = replInitScript;
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

                super.executeScriptUsingStreams(statement, Collections.emptyMap(),
                        VARIABLE_SET_OUTPUT_STREAM, VARIABLE_SET_OUTPUT_STREAM);
            }

        }

        super.executeScriptUsingStreams(script.replace("\n",  " ").replace("\r",  " "),
                variables, stdOutOutputStream, errorOutputStream);
    }

    @Override
    protected void onProcessNotRunningBeforeDispatch(String script) throws ScriptExecutionException {
        throw new ScriptExecutionException(
                "Node.js process exited unexpetedly! Cannot execute script: " + script);

    }


    @Override
    protected Process createProcess(String script, Map<String, Object> variables) throws IOException {

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(Arrays.asList("node", "-e", replInitScript));

        if (variables != null) {

            Map<String, String> environment = processBuilder.environment();

            variables.entrySet().stream().forEach(entry -> {
                String key = entry.getKey();
                Object value = entry.getValue();

                String valueString = String.valueOf(value);

                environment.put(key, valueString);
            });
        }

        return processBuilder.start();
    }

}
