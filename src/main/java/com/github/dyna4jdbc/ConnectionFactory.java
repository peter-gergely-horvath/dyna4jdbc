package com.github.dyna4jdbc;

import com.github.dyna4jdbc.internal.MisconfigurationSQLException;
import com.github.dyna4jdbc.internal.SQLError;
import com.github.dyna4jdbc.internal.common.util.exception.ExceptionUtil;
import com.github.dyna4jdbc.internal.processrunner.jdbc.impl.ProcessRunnerConnection;
import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Properties;

class ConnectionFactory {

	private static final ConnectionFactory INSTANCE = new ConnectionFactory(); // thread-safe: no state is held


	static ConnectionFactory getInstance() {
		return INSTANCE;
	}

	java.sql.Connection newConnection(String factoryConfiguration, Properties info) throws SQLException {
		try {     
			String[] bridgeNameAndConfig = factoryConfiguration.split(":", 2);

			String bridgeName = bridgeNameAndConfig[0];
			String config = bridgeNameAndConfig.length == 2 ? bridgeNameAndConfig[1] : null;

			return newConnection(bridgeName, config, info);
		}
		catch (MisconfigurationSQLException ex)
		{
			String causeMessage = ExceptionUtil.getRootCauseMessage(ex);
			throw SQLError.INVALID_CONFIGURATION.raiseSQLException(ex, causeMessage);
		}
		catch (Exception ex)
		{
			String causeMessage = ExceptionUtil.getRootCauseMessage(ex);
			throw SQLError.CONNECT_FAILED_GENERIC.raiseSQLException(ex, causeMessage);
		}
	}


	private java.sql.Connection newConnection(String bridgeName, String config, Properties info) throws Exception {
		try {
			BuiltInConnectionBridge connectionBridge = BuiltInConnectionBridge.getByName(bridgeName);

			final Class<?> classToInstantiate = (connectionBridge != null) ? connectionBridge.clazz :
				Class.forName(bridgeName);

			Constructor<?> constructor = classToInstantiate.getConstructor(String.class, Properties.class);

			return (java.sql.Connection) constructor.newInstance(config, info);
		}
		catch(InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			if (cause instanceof RuntimeException)
				throw (RuntimeException) cause;
			else if (cause instanceof Error)
				throw (Error) cause;
			else
				throw (Exception)cause;
		}
	}

	private enum BuiltInConnectionBridge {
		SCRIPTENGINE("scriptengine", ScriptEngineConnection.class),
		PROCESS_RUNNER("process-runner", ProcessRunnerConnection.class);

		private final String name;
		private final Class<? extends java.sql.Connection> clazz;

		BuiltInConnectionBridge(String name, Class<? extends java.sql.Connection> clazz) {
			this.name = name;
			this.clazz = clazz;
		}

		private static BuiltInConnectionBridge getByName(String name) {
			for (BuiltInConnectionBridge bridge : BuiltInConnectionBridge.values()) {
				if (bridge.name.equalsIgnoreCase(name)) {
					return bridge;
				}
			}

			return null;
		}
	}


}
