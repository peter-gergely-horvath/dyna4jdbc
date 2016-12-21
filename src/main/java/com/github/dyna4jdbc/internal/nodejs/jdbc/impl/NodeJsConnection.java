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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.UUID;

import com.github.dyna4jdbc.internal.common.jdbc.generic.GenericDatabaseMetaData;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.config.impl.ConfigurationEntry;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessRunnerConnection;

public final class NodeJsConnection extends ProcessRunnerConnection {

    private static final String END_OF_STREAM_INDICATOR = 
            String.format("__dyna4jdbc_eos_token_%s", UUID.randomUUID().toString());
    
    private static final String REPL_CONFIG_SCRIPT = new StringBuilder()
        .append("node -e \"")
            .append("const endOfStreamIndicator = '")
                    .append(END_OF_STREAM_INDICATOR).append("';")
            .append("const vm = require('vm');                          ")
            .append("require('repl').start({                            ")
            .append("   terminal: false,                                ")
            .append("   prompt: '',                                     ")
            .append("   ignoreUndefined: true,                          ")
            .append("   eval: function(cmd, ctx, fn, cb) {              ")
            .append("           try {                                   ")
            .append("               vm.runInContext(cmd, ctx, fn);      ")
            .append("           } catch (err)  {                        ")
            .append("               cb(err);                            ")
            .append("           } finally {                             ")
            .append("               console.log(endOfStreamIndicator);  ")
            .append("           }                                       ")
            .append("       }                                           ")
            .append("   });                                             ")
            .append("\"")
        .toString();


    public NodeJsConnection(
            String parameters,
            Properties properties)
            throws SQLException, MisconfigurationException {

        super(parameters, addNodeJsProperties(properties), NodeJsProcessScriptExecutorFactory.getInstance());

        try (Statement statement = this.createStatement()) {
            statement.executeUpdate(REPL_CONFIG_SCRIPT);
        }
    }

    private static Properties addNodeJsProperties(Properties properties) {

        properties.put(ConfigurationEntry.ENF_OF_DATA_REGEX.getKey(), END_OF_STREAM_INDICATOR);

        return properties;
    }

    @Override
    protected DatabaseMetaData getMetaDataInternal() throws SQLException {

        return new GenericDatabaseMetaData(this,
                System.getProperty("os.name"),
                System.getProperty("os.version"));
    }

}
