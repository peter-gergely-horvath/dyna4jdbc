package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.sql.SQLException;
import java.util.Properties;

public final class RenjinScriptEngineConnection extends DefaultScriptEngineConnection {

    public RenjinScriptEngineConnection(String parameters, Properties properties) throws SQLException,
            MisconfigurationException {
        super(parameters, properties);
    }

//    /**
//     * Renjin script cannot be run by adding the variables into the engineContext as attributes, but adding it to the
//     * Engine's Bindings object.
//     * Therefore, we use this workaround to add the values directly to the Bindings object.
//     *
//     * @param variables the key value pairs
//     * @param engineContext the context of the script engine
//     */
//    @Override
//    protected void applyVariablesToEngineScope(Map<String, Object> variables, ScriptContext engineContext) {
//        if (variables != null) {
//            Bindings bindings = getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
//            for (Map.Entry<String, Object> entry : variables.entrySet()) {
//                String key = entry.getKey();
//                Object value = entry.getValue();
//                bindings.put(key, value);
//            }
//        }
//    }
//
//    /**
//     * There is a NYI UnsupportedOperationException in org.renjin.script.RenjinBindings.remove(Object key) method.
//     * Therefore, until it will be implemented we will use this workaround to "remove" the values
//          assigned to the keys.
//     *
//     * @param variables the key value pairs
//     * @param engineContext the context of the script engine
//     */
//    @Override
//    protected void removeVariablesFromEngineScope(Map<String, Object> variables, ScriptContext engineContext) {
//        if (variables != null) {
//            Bindings bindings = getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
//            for (String key : variables.keySet()) {
//                bindings.put(key, null);
//            }
//        }
//    }
}
