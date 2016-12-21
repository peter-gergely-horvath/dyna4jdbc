package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultExternalProcessScriptExecutorFactory;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ExternalProcessScriptExecutor;

public final class NodeJsProcessScriptExecutorFactory extends DefaultExternalProcessScriptExecutorFactory {
    
    private static final NodeJsProcessScriptExecutorFactory INSTANCE = 
            new NodeJsProcessScriptExecutorFactory();
    
    public static NodeJsProcessScriptExecutorFactory getInstance() {
        return INSTANCE;
    }
    
    @Override
    public ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration) {
        return new NodeJsProcessScriptExecutor(configuration);
    }

}
