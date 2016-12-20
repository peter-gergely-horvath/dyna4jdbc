package com.github.dyna4jdbc.internal.processrunner.jdbc.impl;

import com.github.dyna4jdbc.internal.OutputCapturingScriptExecutor;

public interface ProcessScriptExecutor extends OutputCapturingScriptExecutor {

    void close();
}
