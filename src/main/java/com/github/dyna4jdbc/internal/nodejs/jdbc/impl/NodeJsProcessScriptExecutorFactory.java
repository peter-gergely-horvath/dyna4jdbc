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

    private String replConfigScript;
    
    private NodeJsProcessScriptExecutorFactory(String eosToken) {
        this.replConfigScript = new StringBuilder()
            .append("node -e \"")
                .append("const endOfStreamIndicator = '")
                        .append(eosToken).append("';")
                .append("const vm = require('vm');                          ")
                .append("require('repl').start({                            ")
                .append("   terminal: false,                                ")
                .append("   prompt: '',                                     ")
                .append("   ignoreUndefined: true,                          ")
                .append("   eval: function(cmd, ctx, fn, cb) {              ")
                .append("           try {                                   ")
                .append("               vm.runInContext(cmd, ctx, fn);      ")
                .append("           } catch (err)  {                        ")
                .append("               cb(err);                            ")
                .append("           } finally {                             ")
                .append("               console.log(endOfStreamIndicator);  ")
                .append("           }                                       ")
                .append("       }                                           ")
                .append("   });                                             ")
                .append("\"")
            .toString();
    }
    
    
    @Override
    public ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            NodeJsProcessScriptExecutor nodeJsProcessScriptExecutor = new NodeJsProcessScriptExecutor(configuration);

            nodeJsProcessScriptExecutor.executeScriptUsingStreams(replConfigScript,
                    null, byteArrayOutputStream, byteArrayOutputStream);

            String caputedOutput = byteArrayOutputStream.toString(configuration.getConversionCharset());

            System.err.println("NodeJsProcessScriptExecutorFactory - captured output: " + caputedOutput);

            return nodeJsProcessScriptExecutor;
        } catch (IOException | ScriptExecutionException e) {
            throw new RuntimeException("Exception initializing NodeJsProcessScriptExecutor", e);
        }

    }

}
