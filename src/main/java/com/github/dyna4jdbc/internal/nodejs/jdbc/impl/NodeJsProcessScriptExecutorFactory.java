package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutorFactory;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ExternalProcessScriptExecutor;

public final class NodeJsProcessScriptExecutorFactory extends DefaultExternalProcessScriptExecutorFactory {
    
    public static NodeJsProcessScriptExecutorFactory getInstance(String eosToken) {
        return new NodeJsProcessScriptExecutorFactory(eosToken);
    }

    private String replStartCommand;
    
    private NodeJsProcessScriptExecutorFactory(String eosToken) {
        this.replStartCommand = "node -e \"const endOfStreamToken = '<EOS_TOKEN>'; "
                + "const vm = require('vm'); "
                + "require('repl').start({ "
                    + "terminal: false, "
                    + "prompt: '', "
                    + "ignoreUndefined: true, "
                    + "eval: function(cmd, ctx, fn, cb) { "
                        + "try { vm.runInContext(cmd, ctx, fn); } "
                        + "catch (err) { cb(err); } "
                        + "finally { console.log(endOfStreamToken); } "
                + "} });\"     ".replace("<EOS_TOKEN>", eosToken);
    }
    
    
    @Override
    public ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            NodeJsProcessScriptExecutor nodeJsProcessScriptExecutor = new NodeJsProcessScriptExecutor(configuration);

            nodeJsProcessScriptExecutor.executeScriptUsingStreams(replStartCommand,
                    null, byteArrayOutputStream, byteArrayOutputStream);

            String caputedOutput = byteArrayOutputStream.toString(configuration.getConversionCharset());

            System.err.println("NodeJsProcessScriptExecutorFactory - captured output: " + caputedOutput);

            return nodeJsProcessScriptExecutor;
        } catch (IOException | ScriptExecutionException e) {
            throw new RuntimeException("Exception initializing NodeJsProcessScriptExecutor", e);
        }

    }

}
