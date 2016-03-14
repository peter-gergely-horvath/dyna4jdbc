package com.github.dyna4jdbc;

import com.github.dyna4jdbc.internal.scriptengine.jdbc.impl.ScriptEngineConnection;

import java.lang.reflect.Constructor;
import java.util.Properties;

class ConnectionFactory {

    private static final ConnectionFactory INSTANCE = new ConnectionFactory(); // thread-safe: no state is held


    static ConnectionFactory getInstance() {
        return INSTANCE;
    }

    java.sql.Connection newConnection(String bridgeName, String config, Properties info) throws Exception {
        BuiltInConnectionBridge connectionBridge = BuiltInConnectionBridge.getByName(bridgeName);

        final Class<?> classToInstantiate = (connectionBridge != null) ? connectionBridge.clazz :
                Class.forName(bridgeName);

        Constructor<?> constructor = classToInstantiate.getConstructor(String.class, Properties.class);

        return (java.sql.Connection) constructor.newInstance(config, info);
    }


    private enum BuiltInConnectionBridge {
        SCRIPTENGINE("scriptengine", ScriptEngineConnection.class);

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
