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

 
package com.github.dyna4jdbc.internal.common.jdbc.base;


import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingPreparedStatement;
import com.github.dyna4jdbc.internal.common.jdbc.generic.OutputHandlingStatement;
import com.github.dyna4jdbc.internal.common.outputhandler.ScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.outputhandler.impl.DefaultScriptOutputHandlerFactory;
import com.github.dyna4jdbc.internal.common.typeconverter.ColumnHandlerFactory;
import com.github.dyna4jdbc.internal.common.util.io.DisallowAllWritesOutputStream;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ScriptConnection extends AbstractConnection {

    protected final void executeInitScript(String initScriptPath) throws SQLException, MisconfigurationException {
        File initScript = new File(initScriptPath);
        if (!initScript.exists()) {
            throw MisconfigurationException.forMessage("InitScript not found: %s", initScriptPath);
        }

        try {
            byte[] bytesRead = Files.readAllBytes(initScript.toPath());
            String initScriptText = new String(bytesRead, this.getConfiguration().getConversionCharset());

            getScriptExecutor().executeScriptUsingStreams(
                    initScriptText,
                    null,
                    new DisallowAllWritesOutputStream("An init script cannot generate output"),
                    new DisallowAllWritesOutputStream("An init script cannot generate output"));


        } catch (IOException e) {
            throw JDBCError.INITSCRIPT_READ_IO_ERROR.raiseSQLException(initScriptPath);
        } catch (ScriptExecutionException e) {
            throw JDBCError.INITSCRIPT_EXECUTION_EXCEPTION.raiseSQLException(e, initScriptPath);
        }
    }

    @Override
    protected final Statement createStatementInternal() throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(getColumnHandlerFactory(), getConfiguration());

        return new OutputHandlingStatement<>(
                this, outputHandlerFactory, getScriptExecutor());
    }

    @Override
    protected final PreparedStatement prepareStatementInternal(String script) throws SQLException {

        ScriptOutputHandlerFactory outputHandlerFactory =
                new DefaultScriptOutputHandlerFactory(getColumnHandlerFactory(), getConfiguration());

        return new OutputHandlingPreparedStatement<>(
                script, this, outputHandlerFactory, getScriptExecutor());
    }


    protected abstract ColumnHandlerFactory getColumnHandlerFactory();

    protected abstract Configuration getConfiguration();

    protected abstract OutputCapturingScriptExecutor getScriptExecutor();

}
