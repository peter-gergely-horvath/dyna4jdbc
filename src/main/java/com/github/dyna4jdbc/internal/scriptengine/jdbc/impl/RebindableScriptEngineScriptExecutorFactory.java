package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;


import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.sql.SQLException;

final class RebindableScriptEngineScriptExecutorFactory implements ScriptEngineScriptExecutorFactory {

    private final Configuration configuration;

    private RebindableScriptEngineScriptExecutorFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    static RebindableScriptEngineScriptExecutorFactory getInstance(Configuration configuration) {
        return new RebindableScriptEngineScriptExecutorFactory(configuration);
    }


    @Override
    public ScriptEngineScriptExecutor newScriptEngineScriptExecutor(String scriptEngineName)
            throws SQLException, MisconfigurationException {

        BasicScriptEngineScriptExecutorFactory scriptExecutorFactory =
                BasicScriptEngineScriptExecutorFactory.getInstance(configuration);

        ScriptEngineScriptExecutor delegate = scriptExecutorFactory.newScriptEngineScriptExecutor(scriptEngineName);

        return new RebindableScriptEngineScriptExecutor(delegate, configuration);
    }
}
