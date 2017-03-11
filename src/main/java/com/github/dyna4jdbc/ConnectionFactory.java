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

 
package com.github.dyna4jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.RuntimeDyna4JdbcException;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtils;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.nodejs.jdbc.impl.NodeJsConnection;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessRunnerConnection;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

class ConnectionFactory {

    private static final ConnectionFactory INSTANCE = new ConnectionFactory(); // thread-safe: no state is held

    static ConnectionFactory getInstance() {
        return INSTANCE;
    }

    Connection newConnection(String factoryConfiguration, Properties properties) throws SQLException {
        try {
            String[] bridgeNameAndConfig = factoryConfiguration.split(":", 2);

            String connectionType = bridgeNameAndConfig[0].toLowerCase(Locale.ENGLISH);
            String config = ArrayUtils.tryGetByIndex(bridgeNameAndConfig, 1);

            return newConnection(connectionType, config, properties);

        } catch (MisconfigurationException ex) {
            String causeMessage = ExceptionUtils.getRootCauseMessage(ex);
            throw JDBCError.INVALID_CONFIGURATION.raiseSQLException(ex, causeMessage);

        } catch (SQLException sqlEx) {
            // re-throw SQLExceptions received from lower layers: we expect that
            // their message will be detailed enough for the user to understand.
            throw sqlEx;

        } catch (RuntimeDyna4JdbcException ex) {
            throw new SQLException(ex.getMessage(), ex.getSqlState(), ex);

        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            throw JDBCError.REQUIRED_RESOURCE_UNAVAILABLE.raiseSQLException(ex, ex.getMessage());

        } catch (Exception ex) {
            String causeMessage = ExceptionUtils.getRootCauseMessage(ex);
            throw JDBCError.CONNECT_FAILED_EXCEPTION.raiseSQLException(ex, causeMessage);

        } catch (Throwable throwable) {
            /*
            We do not trust any external library here: some script languages
            might e.g. attempt to load native libraries, which can cause an
            UnsatisfiedLinkError. While a GUI client application might be prepared
            to properly handle SQLExceptions, it might break if we let any unexpected
            Throwables escape the driver layer.

            Hence, we intentionally and knowingly catch all Throwables (including
            Errors) and wrap them into an SQLException.
            */
            String causeMessage = ExceptionUtils.getRootCauseMessage(throwable);
            throw JDBCError.UNEXPECTED_THROWABLE.raiseSQLException(throwable, causeMessage);
        }
    }


    protected Connection newConnection(String connectionType, String config, Properties properties) throws Exception {

        switch (connectionType) {
            case "process-runner":
                return new ProcessRunnerConnection(config, properties);


            case "scriptengine":
                return new ScriptEngineConnection(config, properties);


            case "nodejs":
                return new NodeJsConnection(config, properties);


            default:
                throw MisconfigurationException.forMessage("No such connection type: '%s'", connectionType);
        }
    }

}
