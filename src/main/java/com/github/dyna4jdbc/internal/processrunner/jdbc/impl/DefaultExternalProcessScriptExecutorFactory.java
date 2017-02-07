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
