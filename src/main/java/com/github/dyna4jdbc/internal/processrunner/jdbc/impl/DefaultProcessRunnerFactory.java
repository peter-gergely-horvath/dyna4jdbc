package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import com.github.dyna4jdbc.internal.config.Configuration;

public class DefaultProcessRunnerFactory implements ProcessRunnerFactory {
    
    private static final DefaultProcessRunnerFactory INSTANCE = new DefaultProcessRunnerFactory();
    
    public static DefaultProcessRunnerFactory getInstance() {
        return INSTANCE;
    }
    
    //CHECKSTYLE.OFF: DesignForExtension : incorrect detection of "is not designed for extension"
    @Override
    public ProcessScriptExecutor newProcessScriptExecutor(Configuration configuration) {
        return new DefaultProcessRunnerScriptExecutor(configuration);
    }
    //CHECKSTYLE.ON: DesignForExtension : incorrect detection of "is not designed for extension"

}
