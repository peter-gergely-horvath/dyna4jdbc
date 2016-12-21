package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

public class DefaultExternalProcessScriptExecutorFactory implements ExternalProcessScriptExecutorFactory {
    
    private static final DefaultExternalProcessScriptExecutorFactory INSTANCE = 
            new DefaultExternalProcessScriptExecutorFactory();
    
    public static DefaultExternalProcessScriptExecutorFactory getInstance() {
        return INSTANCE;
    }
    
    //CHECKSTYLE.OFF: DesignForExtension : incorrect detection of "is not designed for extension"
    @Override
    public ExternalProcessScriptExecutor newExternalProcessScriptExecutor(Configuration configuration) {
        return new DefaultExternalProcessScriptExecutor(configuration);
    }
    //CHECKSTYLE.ON: DesignForExtension

}
