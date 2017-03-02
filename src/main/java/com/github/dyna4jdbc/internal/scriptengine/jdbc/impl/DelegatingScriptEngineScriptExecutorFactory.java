package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;


import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.sql.SQLException;

final class DelegatingScriptEngineScriptExecutorFactory implements ScriptEngineScriptExecutorFactory {

    private final Configuration configuration;

    private DelegatingScriptEngineScriptExecutorFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    static DelegatingScriptEngineScriptExecutorFactory getInstance(Configuration configuration) {
        return new DelegatingScriptEngineScriptExecutorFactory(configuration);
    }


    @Override
    public ScriptEngineScriptExecutor newScriptEngineScriptExecutor(String scriptEngineName)
            throws SQLException, MisconfigurationException {

        DefaultScriptEngineScriptExecutorFactory scriptExecutorFactory =
                DefaultScriptEngineScriptExecutorFactory.getInstance(configuration);

        ScriptEngineScriptExecutor delegate = scriptExecutorFactory.newScriptEngineScriptExecutor(scriptEngineName);

        return new DelegatingScriptEngineScriptExecutor(delegate, configuration);
    }
}
