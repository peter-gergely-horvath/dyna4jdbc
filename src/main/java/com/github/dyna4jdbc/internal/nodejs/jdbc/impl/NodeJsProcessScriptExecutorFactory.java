package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutorFactory;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ExternalProcessScriptExecutor;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessExecutionException;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessManager;

public final class NodeJsProcessScriptExecutorFactory extends DefaultExternalProcessScriptExecutorFactory {

    public static NodeJsProcessScriptExecutorFactory getInstance(String eosToken) {
        return new NodeJsProcessScriptExecutorFactory(eosToken);
    }

    private final String replStartCommand;

    // TODO: cleanup
    private NodeJsProcessScriptExecutorFactory(String eosToken) {
        this.replStartCommand = "const endOfStreamToken = '" + eosToken + "'; " + "const vm = require('vm'); "
                + "require('repl').start({ " + "terminal: false, " + "prompt: '', " + "ignoreUndefined: true, "
                + "eval: function(cmd, ctx, fn, cb) { " + "try { vm.runInContext(cmd, ctx, fn); } "
                + "catch (err) { cb(err); } " + "finally { console.log(endOfStreamToken); } " + "} });";
    }

    @Override
    public ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            NodeJsProcessScriptExecutor nodeJsProcessScriptExecutor = new NodeJsProcessScriptExecutor(configuration) {

                @Override
                protected ProcessManager createProcessManager(String script, Map<String, Object> variables)
                        throws ProcessExecutionException {

                    try {
                        ProcessBuilder processBuilder = new ProcessBuilder();
                        processBuilder.command(Arrays.asList("node", "-e", replStartCommand));

                        if (variables != null) {
                            
                            Map<String, String> environment = processBuilder.environment();
                            
                            variables.entrySet().stream().forEach(entry -> {
                                String key = entry.getKey();
                                Object value = entry.getValue();
                                
                                String valueString = String.valueOf(value);
                                
                                environment.put(key, valueString);
                            });
                        }

                        Process process = processBuilder.start();
                        return ProcessManager.start(
                                process, script, variables, getConfiguration(), getExecutorService());

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            nodeJsProcessScriptExecutor.executeScriptUsingStreams(replStartCommand, null, byteArrayOutputStream,
                    byteArrayOutputStream);

            // TODO: implement handling of unexpected output showing up here
            String caputedOutput = byteArrayOutputStream.toString(configuration.getConversionCharset());

            System.err.println("NodeJsProcessScriptExecutorFactory - captured output: " + caputedOutput);

            return nodeJsProcessScriptExecutor;
        } catch (IOException | ScriptExecutionException e) {
            throw new RuntimeException("Exception initializing NodeJsProcessScriptExecutor", e);
        }

    }

}
