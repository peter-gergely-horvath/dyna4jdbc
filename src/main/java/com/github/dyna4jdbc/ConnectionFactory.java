package com.github.dyna4jdbc;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtil;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessRunnerConnection;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScalaScriptEngineConnection;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Properties;
import java.util.Locale;

class ConnectionFactory {

    private static final ConnectionFactory INSTANCE = new ConnectionFactory(); // thread-safe: no state is held

    static ConnectionFactory getInstance() {
        return INSTANCE;
    }

    Connection newConnection(String factoryConfiguration, Properties info) throws SQLException {
        try {
            String[] bridgeNameAndConfig = factoryConfiguration.split(":", 2);

            String bridgeName = bridgeNameAndConfig[0].toLowerCase(Locale.ENGLISH);
            String config;

            if (bridgeNameAndConfig.length == 2) {
                config = bridgeNameAndConfig[1];
            } else {
                config = null;
            }

            return newConnection(bridgeName, config, info);

        } catch (MisconfigurationException ex) {
            String causeMessage = ExceptionUtil.getRootCauseMessage(ex);
            throw JDBCError.INVALID_CONFIGURATION.raiseSQLException(ex, causeMessage);
        } catch (Exception ex) {
            String causeMessage = ExceptionUtil.getRootCauseMessage(ex);
            throw JDBCError.CONNECT_FAILED_EXCEPTION.raiseSQLException(ex, causeMessage);
        }
    }


    protected Connection newConnection(String connectionType, String config, Properties info) throws Exception {

        try {

        Class<? extends Connection> connectionClass;

        switch (connectionType) {
            case "process-runner":
                connectionClass = ProcessRunnerConnection.class;
                break;

            case "scriptengine":
                if (config == null || !config.toLowerCase(Locale.ENGLISH).startsWith("scala")) {
                    connectionClass = ScriptEngineConnection.class;
                } else {
                    connectionClass = ScalaScriptEngineConnection.class;
                }

                break;

            default:
                throw MisconfigurationException.forMessage("No such connection type: '%s'", connectionType);
        }

            Constructor<? extends Connection> connectionConstructor =
                    connectionClass.getConstructor(String.class, Properties.class);

            return connectionConstructor.newInstance(config, info);


        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw (Exception) cause;
            }
        }
    }


}
