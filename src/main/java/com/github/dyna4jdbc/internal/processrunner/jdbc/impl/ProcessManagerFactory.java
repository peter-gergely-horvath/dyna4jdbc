package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.util.concurrent.ExecutorService;

import com.github.dyna4jdbc.internal.config.Configuration;

final class ProcessManagerFactory {

    private final Configuration configuration;
    private final ExecutorService executorService;

    private ProcessManagerFactory(Configuration configuration, ExecutorService executorService) {
        this.configuration = configuration;
        this.executorService = executorService;
        
    }
    
    static ProcessManagerFactory getInstance(Configuration configuration, ExecutorService executorService) {
        return new ProcessManagerFactory(configuration, executorService);
    }
    
    
    ProcessManager newProcessManager(Process process) throws ProcessExecutionException {
        return ProcessManager.newInstance(process, configuration, executorService);
    }

}
