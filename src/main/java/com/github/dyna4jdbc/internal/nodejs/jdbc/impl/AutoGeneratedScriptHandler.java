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

 
package com.github.dyna4jdbc.internal.nodejs.jdbc.impl;

import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.ScriptExecutor;
import com.github.dyna4jdbc.internal.common.outputhandler.IOHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultIOHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.sqlwarning.SQLWarningCollectorSink;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.io.OutputStream;
import java.sql.SQLWarning;
import java.util.List;

/**
 * A support class, which is intended to be used for auto-generated scripts, where
 * any warnings (writes to standard ERROR) should immediately be considered as a
 * fatal error. For example, a script, which initializes the Node.js session
 * should complete without any errors or warnings being emitted to to standard ERROR.   
 *  
 * @author Horvath Peter
 */
abstract class AutoGeneratedScriptHandler {
    
    private final Configuration configuration;
    private final ScriptExecutor scriptExecutor;

    AutoGeneratedScriptHandler(Configuration configuration, ScriptExecutor scriptExecutor) {
        this.configuration = configuration;
        this.scriptExecutor = scriptExecutor;
    }

    void invokeScript(String script) throws ScriptExecutionException {

        IOHandlerFactory ioHandlerFactory = DefaultIOHandlerFactory.getInstance(configuration);

        SQLWarningCollectorSink sqlWarningSink = new SQLWarningCollectorSink();

        OutputStream outputWarningSinkOutputStream =
            ioHandlerFactory.newWarningSinkOutputStream(sqlWarningSink);
        OutputStream errorWarningSinkOutputStream =
            ioHandlerFactory.newWarningSinkOutputStream(sqlWarningSink);

        this.scriptExecutor.executeScript(script, null,
                outputWarningSinkOutputStream, errorWarningSinkOutputStream);

        List<SQLWarning> warningList = sqlWarningSink.getWarnings();

        switch (warningList.size()) {
            case 0:
                // NO warnings: OK
                break;

            case 1:
                // ONE warning - use it as root cause
                onSingleWarning(script, warningList.get(0));
                break;

            default:
                // MULTIPLE warnings - use them as suppressed
                onMultipleWarnings(script, warningList);
        }

    }

    protected abstract void onSingleWarning(String script, SQLWarning warning);

    protected abstract void onMultipleWarnings(String script, List<SQLWarning> warningList);
}
