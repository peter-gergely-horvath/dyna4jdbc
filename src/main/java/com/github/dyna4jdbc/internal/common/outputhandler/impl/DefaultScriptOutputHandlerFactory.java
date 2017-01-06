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

 
package com.github.dyna4jdbc.internal.common.outputhandler.impl;

import com.github.dyna4jdbc.internal.common.outputhandler.*;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.sql.Statement;

public class DefaultScriptOutputHandlerFactory implements ScriptOutputHandlerFactory {

    private final ColumnHandlerFactory columnHandlerFactory;
    private final Configuration configuration;

    public DefaultScriptOutputHandlerFactory(ColumnHandlerFactory columnHandlerFactory, Configuration configuration) {
        this.columnHandlerFactory = columnHandlerFactory;
        this.configuration = configuration;

    }

    //CHECKSTYLE.OFF: DesignForExtension
    @Override
    public SingleResultSetScriptOutputHandler newSingleResultSetQueryScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink, int maxRows) {

        return new DefaultSingleResultSetScriptOutputHandler(
                statement, columnHandlerFactory, configuration, warningSink, maxRows);
    }

    @Override
    public MultipleResultSetScriptOutputHandler newUpdateOrQueryScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink,
            int maxRows) {

        return new DefaultMultipleResultSetScriptOutputHandler(
                statement, columnHandlerFactory, configuration, warningSink, maxRows);
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink) {

        return new DefaultUpdateScriptOutputHandler(this.configuration, warningSink);
    }
    //CHECKSTYLE.ON: DesignForExtension


}
