package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.DefaultProcessRunnerFactory;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessScriptExecutor;

public final class NodeJsProcessRunnerFactory extends DefaultProcessRunnerFactory {
    
    private static final NodeJsProcessRunnerFactory INSTANCE = new NodeJsProcessRunnerFactory();
    
    public static NodeJsProcessRunnerFactory getInstance() {
        return INSTANCE;
    }
    
    @Override
    public ProcessScriptExecutor newProcessScriptExecutor(Configuration configuration) {
        return new NodeJsProcessRunner(super.newProcessScriptExecutor(configuration));
    }

}
