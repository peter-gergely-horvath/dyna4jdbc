package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;


import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;
import com.github.dyna4jdbc.internal.ScriptExecutionException;
import com.github.dyna4jdbc.internal.config.Configuration;
import com.github.dyna4jdbc.internal.config.MisconfigurationException;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

final class DelegatingScriptEngineScriptExecutor implements ScriptEngineScriptExecutor {

    private final AtomicReference<ScriptEngineScriptExecutor> delegateRef = new AtomicReference<>();

    private final ConcurrentHashMap<String, ScriptEngineScriptExecutor> scriptExecutorMap = new ConcurrentHashMap<>();

    private final DefaultScriptEngineScriptExecutorFactory scriptEngineScriptExecutorFactory;

    DelegatingScriptEngineScriptExecutor(ScriptEngineScriptExecutor delegate, Configuration configuration) {

        this.delegateRef.set(delegate);
        this.scriptEngineScriptExecutorFactory = DefaultScriptEngineScriptExecutorFactory.getInstance(configuration);
    }

    @Override
    public void executeScriptUsingStreams(
            String script, Map<String, Object> variables,
            OutputStream stdOutOutputStream,
            OutputStream errorOutputStream) throws ScriptExecutionException {

        if (script.contains("javascript")) {
            // FIXME: implement me
            rebindDelegate("javascript");

        } else {
            getDelegate().executeScriptUsingStreams(
                    script, variables, stdOutOutputStream, errorOutputStream);
        }
    }

    private void rebindDelegate(String newScriptExecutorSystemName) {

        ScriptEngineScriptExecutor current = getDelegate();

        String systemName = current.getSystemName();

        scriptExecutorMap.put(systemName, current);

        ScriptEngineScriptExecutor newScriptExecutor = scriptExecutorMap.computeIfAbsent(newScriptExecutorSystemName,

                scriptEngineName -> {
            try {
                return this.scriptEngineScriptExecutorFactory.newScriptEngineScriptExecutor(scriptEngineName);
            } catch (SQLException | MisconfigurationException ex) {
                throw JDBCError.LOADING_SCRIPTENGINE_FAILED.raiseUncheckedException(ex);
            }
        });

        if (!delegateRef.compareAndSet(current, newScriptExecutor)) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE.raiseUncheckedException(
                    String.format("failed to compareAndSet(%s, %s)", current, newScriptExecutor));
        }

    }

    @Override
    public void cancel() throws CancelException {
        getDelegate().cancel();
    }

    @Override
    public String getSystemName() {
        return getDelegate().getSystemName();
    }

    @Override
    public String getHumanFriendlyName() {
        return getDelegate().getHumanFriendlyName();
    }

    @Override
    public String getVersion() {
        return getDelegate().getVersion();
    }

    private ScriptEngineScriptExecutor getDelegate() {
        ScriptEngineScriptExecutor theDelegate = delegateRef.get();
        if (theDelegate == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE
                    .raiseUncheckedException("theDelegate is null");
        }
        return theDelegate;
    }
}
