package com.github.dyna4jdbc.internal;

import java.io.OutputStream;
import java.io.Writer;

public interface OutputCapturingScriptExecutor {

    void executeScriptUsingCustomWriters(
            String script, OutputStream stdOutputStream, OutputStream errorOutputStream) throws ScriptExecutionException;
}
