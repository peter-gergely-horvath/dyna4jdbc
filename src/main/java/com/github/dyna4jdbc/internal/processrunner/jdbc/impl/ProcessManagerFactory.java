/*
 * Copyright (c) 2016, 2017 Peter G. Horvath, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import java.util.concurrent.ExecutorService;

import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
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
    

    ProcessManager newProcessManager(Process process, SQLWarningSink warningSink) throws ProcessExecutionException {
        return ProcessManager.newInstance(process, configuration, executorService, warningSink);
    }

}
