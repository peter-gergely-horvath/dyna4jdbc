package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import java.sql.SQLException;
import java.util.Properties;

import javax.script.Bindings;
import javax.script.ScriptContext;

import com.github.dyna4jdbc.internal.config.MisconfigurationException;

// TODO: remove this work-around class, once Renjin ScriptEngin is fixed
public final class RenjinScriptEngineConnection extends DefaultScriptEngineConnection {

    public RenjinScriptEngineConnection(String parameters, Properties properties) throws SQLException,
            MisconfigurationException {
        super(parameters, properties);
    }
    
    @Override
    protected Bindings getBindings(ScriptContext engineContext) {
        // Work-around for Renjin ScriptEngine issue
        return getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
    }
}
