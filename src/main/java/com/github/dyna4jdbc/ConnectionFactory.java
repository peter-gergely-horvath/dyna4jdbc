package com.github.dyna4jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Properties;

import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.common.util.collection.ArrayUtils;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtils;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessRunnerConnection;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.DefaultScriptEngineConnection;

class ConnectionFactory {

    private static final ConnectionFactory INSTANCE = new ConnectionFactory(); // thread-safe: no state is held

    static ConnectionFactory getInstance() {
        return INSTANCE;
    }

    Connection newConnection(String factoryConfiguration, Properties info) throws SQLException {
        try {
            String[] bridgeNameAndConfig = factoryConfiguration.split(":", 2);

            String bridgeName = bridgeNameAndConfig[0].toLowerCase(Locale.ENGLISH);
            String config = ArrayUtils.tryGetByIndex(bridgeNameAndConfig, 1);

            return newConnection(bridgeName, config, info);

        } catch (MisconfigurationException ex) {
            String causeMessage = ExceptionUtils.getRootCauseMessage(ex);
            throw JDBCError.INVALID_CONFIGURATION.raiseSQLException(ex, causeMessage);

        } catch (SQLException sqlEx) {
            // re-throw SQLExceptions received from lower layers: we expect that
            // their message will be detailed enough for the user to understand.
            throw sqlEx;

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


    protected Connection newConnection(String connectionType, String config, Properties info) throws Exception {

        try {

        Class<? extends Connection> connectionClass;

        switch (connectionType) {
            case "process-runner":
                connectionClass = ProcessRunnerConnection.class;
                break;

            case "scriptengine":
                connectionClass = getScriptEngineConnectionClassForConfiguration(connectionType, config);
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

    private Class<? extends Connection> getScriptEngineConnectionClassForConfiguration(
            String connectionType, String config) throws MisconfigurationException {

        Class<? extends Connection> connectionClass;
        if (config == null || config.matches("\\s+:")) {
            throw MisconfigurationException.forMessage(
                    "ScriptEngine name must be specified", connectionType);
        }

        String scriptEngineName = config.toLowerCase(Locale.ENGLISH).split(":")[0];
        connectionClass = getScriptEngineConnectionClassForName(scriptEngineName);
        return connectionClass;
    }

    private Class<? extends Connection> getScriptEngineConnectionClassForName(String lowerCaseScriptEngineName) {

        /*
         * Some of the ScriptEngines require special handling: this is the place to implement selection.
         *
         * Currently, we do not have any special case; however this method is retained _for now_.
         */
        return DefaultScriptEngineConnection.class;
    }

}
