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

 
package com.github.dyna4jdbc.internal.scriptengine.jdbc.impl;

import com.github.dyna4jdbc.internal.CancelException;
import com.github.dyna4jdbc.internal.JDBCError;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

abstract class DelegatingScriptExecutor implements ScriptEngineScriptExecutor {

    private final AtomicReference<ScriptEngineScriptExecutor> delegateRef = new AtomicReference<>();

    protected DelegatingScriptExecutor(ScriptEngineScriptExecutor delegate) {
        this.delegateRef.set(delegate);
    }

    protected final ScriptEngineScriptExecutor getDelegate() {
        ScriptEngineScriptExecutor theDelegate = delegateRef.get();
        if (theDelegate == null) {
            throw JDBCError.DRIVER_BUG_UNEXPECTED_STATE
                    .raiseUncheckedException("theDelegate is null");
        }
        return theDelegate;
    }


    @Override
    public final void cancel() throws CancelException {
        getDelegate().cancel();
    }

    @Override
    public final void setVariables(Map<String, Object> variables) {
        getDelegate().setVariables(variables);
    }

    @Override
    public final Map<String, Object> getVariables() {
        return getDelegate().getVariables();
    }

    @Override
    public final String getSystemName() {
        return getDelegate().getSystemName();
    }

    @Override
    public final String getHumanFriendlyName() {
        return getDelegate().getHumanFriendlyName();
    }

    @Override
    public final String getVersion() {
        return getDelegate().getVersion();
    }

    final boolean compareAndSetDelegate(
            ScriptEngineScriptExecutor expectedCurrent, ScriptEngineScriptExecutor newScriptExecutor) {
        return delegateRef.compareAndSet(expectedCurrent, newScriptExecutor);
    }
}
