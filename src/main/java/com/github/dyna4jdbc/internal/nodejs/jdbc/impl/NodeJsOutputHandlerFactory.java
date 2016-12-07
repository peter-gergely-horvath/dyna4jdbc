/*
 * Copyright (c) 2016 Peter G. Horvath, All Rights Reserved.
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

import com.github.dyna4jdbc.internal.common.outputhandler.SQLWarningSink;
import com.github.dyna4jdbc.internal.common.outputhandler.UpdateScriptOutputHandler;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.config.Configuration;

import java.sql.Statement;

final class NodeJsOutputHandlerFactory extends DefaultScriptOutputHandlerFactory {

    private final Configuration configuration;

    NodeJsOutputHandlerFactory(ColumnHandlerFactory columnHandlerFactory, Configuration configuration) {
        super(columnHandlerFactory, configuration);
        this.configuration = configuration;
    }

    @Override
    public UpdateScriptOutputHandler newUpdateScriptOutputHandler(
            Statement statement,
            String script,
            SQLWarningSink warningSink) {

        return new NodeJsUpdateScriptOutputHandler(this.configuration, warningSink);
    }
}
