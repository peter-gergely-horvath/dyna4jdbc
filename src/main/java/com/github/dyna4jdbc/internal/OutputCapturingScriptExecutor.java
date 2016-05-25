package com.github.dyna4jdbc.internal;

import java.io.OutputStream;

public interface OutputCapturingScriptExecutor {

    void executeScriptUsingCustomWriters(String script,
                                         OutputStream stdOutputStream,
                                         OutputStream errorOutputStream) throws ScriptExecutionException;
}
